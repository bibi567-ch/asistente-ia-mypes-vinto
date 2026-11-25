import json
import io
import os
import secrets
import threading
from contextlib import asynccontextmanager
from math import gcd
from typing import Annotated

import numpy as np
import soundfile as sf
import torch
import torch.nn.functional as functional
from fastapi import FastAPI, File, Header, HTTPException, UploadFile
from scipy.signal import resample_poly
from transformers import AutoModel, AutoTokenizer, pipeline

MAX_AUDIO_BYTES = 5 * 1024 * 1024
MAX_AUDIO_SECONDS = 120
TARGET_SAMPLE_RATE = 16_000
DEFAULT_ASR_MODEL = "QuechuaBase/whisper-base-qxp-finetuned"
DEFAULT_ASR_REVISION = "5faf249f2a5b0034c095a91b3343c2d670fe5eee"
DEFAULT_INTENT_MODEL = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"
DEFAULT_INTENT_REVISION = "e8f8c211226b894fcb81acc59f3b34ba3efd5f42"
INTENT_MIN_SCORE = 0.62
INTENT_MIN_MARGIN = 0.08

INTENT_EXAMPLES = {
    "registrar_venta": (
        "Quiero registrar una venta.",
        "Anota una venta de este producto.",
        "Quiero vender un producto.",
        "Abrir el formulario para hacer una venta.",
    ),
    "consultar_stock": (
        "Cuánto stock queda de un producto.",
        "Consulta el inventario de productos.",
        "Cuántas unidades quedan disponibles.",
        "Mostrar existencias de un producto.",
    ),
    "consultar_ventas": (
        "Cuánto vendí hoy.",
        "Consulta el total de ventas del día.",
        "Mostrar el resumen de ventas.",
        "Cuánto suman las ventas registradas.",
    ),
    "agregar_producto": (
        "Quiero agregar un producto al inventario.",
        "Registrar un producto nuevo.",
        "Abrir el formulario para añadir un producto.",
    ),
    "consulta_cognitiva": (
        "Tengo una pregunta sobre mi negocio.",
        "Explícame cómo calcular la ganancia.",
        "Quiero una recomendación para administrar mi negocio.",
    ),
    "ayuda": (
        "Qué puedo hacer con esta aplicación.",
        "Ayuda con los comandos disponibles.",
        "Mostrar instrucciones de uso.",
    ),
}


class IntentClassifier:
    def __init__(self, model_id: str, revision: str, examples: dict[str, tuple[str, ...]]) -> None:
        self.tokenizer = AutoTokenizer.from_pretrained(model_id, revision=revision)
        self.model = AutoModel.from_pretrained(model_id, revision=revision)
        self.model.eval()
        self.labels = tuple(examples)
        phrases = [example for samples in examples.values() for example in samples]
        encoded = self._encode(phrases).split([len(samples) for samples in examples.values()])
        self.prototypes = torch.stack(
            [functional.normalize(group.mean(dim=0), p=2, dim=0) for group in encoded]
        )

    def classify(self, text: str) -> tuple[str | None, float | None]:
        if not text.strip():
            return None, None
        embedding = self._encode([text])[0]
        scores = torch.mv(self.prototypes, embedding)
        top_scores, top_indexes = torch.topk(scores, k=2)
        score = float(top_scores[0].item())
        margin = score - float(top_scores[1].item())
        if score < INTENT_MIN_SCORE or margin < INTENT_MIN_MARGIN:
            return None, score
        return self.labels[int(top_indexes[0].item())], score

    def _encode(self, sentences: list[str]) -> torch.Tensor:
        tokens = self.tokenizer(
            sentences,
            padding=True,
            truncation=True,
            max_length=128,
            return_tensors="pt",
        )
        with torch.inference_mode():
            token_embeddings = self.model(**tokens).last_hidden_state
            mask = tokens["attention_mask"].unsqueeze(-1).expand(token_embeddings.size()).float()
            pooled = (token_embeddings * mask).sum(dim=1) / mask.sum(dim=1).clamp(min=1e-9)
            return functional.normalize(pooled, p=2, dim=1)


def load_intent_examples() -> dict[str, tuple[str, ...]]:
    examples = {label: list(phrases) for label, phrases in INTENT_EXAMPLES.items()}
    raw_examples = os.environ.get("INTENT_EXAMPLES_JSON", "")
    if not raw_examples:
        return {label: tuple(phrases) for label, phrases in examples.items()}
    if len(raw_examples.encode("utf-8")) > 64 * 1024:
        raise RuntimeError("INTENT_EXAMPLES_JSON must not exceed 64 KB.")

    try:
        additional_examples = json.loads(raw_examples)
    except json.JSONDecodeError as error:
        raise RuntimeError("INTENT_EXAMPLES_JSON must be valid JSON.") from error
    if not isinstance(additional_examples, dict):
        raise RuntimeError("INTENT_EXAMPLES_JSON must be an object keyed by intent.")

    for label, phrases in additional_examples.items():
        if label not in examples or not isinstance(phrases, list):
            raise RuntimeError(f"Unsupported intent examples entry: {label}.")
        if len(phrases) > 40 or any(
            not isinstance(phrase, str) or not phrase.strip() or len(phrase) > 200
            for phrase in phrases
        ):
            raise RuntimeError(f"Intent examples for {label} must be up to 40 short phrases.")
        examples[label].extend(phrase.strip() for phrase in phrases)
    return {label: tuple(phrases) for label, phrases in examples.items()}


@asynccontextmanager
async def lifespan(application: FastAPI):
    model_id = os.environ.get("ASR_MODEL_ID", DEFAULT_ASR_MODEL).strip()
    model_revision = (
        os.environ.get("ASR_MODEL_REVISION", DEFAULT_ASR_REVISION).strip()
        or DEFAULT_ASR_REVISION
    )
    intent_model_id = os.environ.get("INTENT_MODEL_ID", DEFAULT_INTENT_MODEL).strip()
    intent_model_revision = os.environ.get(
        "INTENT_MODEL_REVISION",
        DEFAULT_INTENT_REVISION,
    ).strip() or DEFAULT_INTENT_REVISION
    api_token = os.environ.get("ASR_API_TOKEN", "").strip()
    if not model_id:
        raise RuntimeError("ASR_MODEL_ID must name an approved speech model.")
    if not intent_model_id:
        raise RuntimeError("INTENT_MODEL_ID must name an approved text embedding model.")
    if not api_token:
        raise RuntimeError("ASR_API_TOKEN is required.")

    application.state.api_token = api_token
    application.state.model_id = model_id
    application.state.transcriber = pipeline(
        task="automatic-speech-recognition",
        model=model_id,
        revision=model_revision,
        device=-1,
        chunk_length_s=30,
        trust_remote_code=False,
    )
    application.state.intent_classifier = IntentClassifier(
        intent_model_id,
        intent_model_revision,
        load_intent_examples(),
    )
    application.state.inference_lock = threading.Lock()
    yield


app = FastAPI(title="Quechua ASR service", lifespan=lifespan)


@app.get("/health")
def health() -> dict[str, str]:
    if getattr(app.state, "transcriber", None) is None:
        raise HTTPException(status_code=503, detail="Model is not ready.")
    return {"status": "ok", "model": app.state.model_id}


@app.post("/transcribe")
def transcribe(
    audio: Annotated[UploadFile, File()],
    authorization: Annotated[str | None, Header()] = None,
) -> dict[str, str]:
    expected_authorization = f"Bearer {app.state.api_token}"
    if authorization is None or not secrets.compare_digest(authorization, expected_authorization):
        raise HTTPException(
            status_code=401,
            detail="Bearer token required.",
            headers={"WWW-Authenticate": "Bearer"},
        )

    audio_bytes = audio.file.read(MAX_AUDIO_BYTES + 1)
    if len(audio_bytes) > MAX_AUDIO_BYTES:
        raise HTTPException(status_code=413, detail="Audio exceeds the 5 MB limit.")
    if not audio_bytes:
        raise HTTPException(status_code=400, detail="Audio file is empty.")

    try:
        audio_info = sf.info(io.BytesIO(audio_bytes))
    except (OSError, RuntimeError, sf.LibsndfileError, ValueError) as error:
        raise HTTPException(status_code=400, detail="Audio must be a supported WAV, FLAC, or OGG file.") from error

    if audio_info.samplerate < 8_000 or audio_info.samplerate > 96_000:
        raise HTTPException(status_code=400, detail="Audio sample rate must be between 8 kHz and 96 kHz.")
    if audio_info.frames / audio_info.samplerate > MAX_AUDIO_SECONDS:
        raise HTTPException(status_code=413, detail="Audio exceeds the 120 second limit.")

    try:
        samples, sample_rate = sf.read(
            io.BytesIO(audio_bytes),
            dtype="float32",
            always_2d=True,
            frames=MAX_AUDIO_SECONDS * audio_info.samplerate + 1,
        )
    except (OSError, RuntimeError, sf.LibsndfileError, ValueError) as error:
        raise HTTPException(status_code=400, detail="Audio could not be decoded.") from error
    if samples.shape[0] > MAX_AUDIO_SECONDS * sample_rate:
        raise HTTPException(status_code=413, detail="Audio exceeds the 120 second limit.")

    mono_samples = samples.mean(axis=1)
    if sample_rate != TARGET_SAMPLE_RATE:
        divisor = gcd(sample_rate, TARGET_SAMPLE_RATE)
        mono_samples = resample_poly(
            mono_samples,
            TARGET_SAMPLE_RATE // divisor,
            sample_rate // divisor,
        ).astype(np.float32, copy=False)

    with app.state.inference_lock:
        result = app.state.transcriber(
            {"raw": mono_samples, "sampling_rate": TARGET_SAMPLE_RATE}
        )
        text = result.get("text")
        if not isinstance(text, str):
            raise HTTPException(
                status_code=502,
                detail="The speech model returned an invalid response.",
            )
        text = text.strip()
        intent, confidence = app.state.intent_classifier.classify(text)
    return {"text": text, "intent": intent, "confidence": confidence}
