import { Router } from 'express';
import { requireAuth } from '../auth/require-auth.js';
import { env } from '../config/env.js';

export const asrRouter = Router();
asrRouter.use(requireAuth);

asrRouter.post('/transcribe', async (request, response) => {
  const provider = env.asrProvider;
  if (provider !== 'google' && provider !== 'whisper' && provider !== 'quechua' && provider !== 'simulated') {
    response.status(503).json({
      error: { code: 'asr_not_configured', message: 'Configure ASR_PROVIDER as google, whisper, quechua, or simulated.' }
    });
    return;
  }

  const contentType = request.header('content-type') ?? '';
  const boundaryMatch = /^multipart\/form-data;\s*boundary=(?:"([^"]+)"|([^;]+))/i.exec(contentType);
  if (!boundaryMatch) {
    response.status(400).json({ error: { code: 'invalid_multipart', message: 'Content-Type must be multipart/form-data.' } });
    return;
  }

  try {
    const chunks: Buffer[] = [];
    let size = 0;
    for await (const chunk of request) {
      size += chunk.length;
      if (size > 5 * 1024 * 1024) {
        response.status(413).json({ error: { code: 'audio_too_large', message: 'Audio exceeds the 5 MB limit.' } });
        return;
      }
      chunks.push(Buffer.isBuffer(chunk) ? chunk : Buffer.from(chunk));
    }
    const audio = extractMultipartAudio(Buffer.concat(chunks), boundaryMatch[1] ?? boundaryMatch[2]);
    if (!audio) {
      response.status(400).json({ error: { code: 'audio_missing', message: 'Multipart field "audio" must contain an audio file.' } });
      return;
    }

    if (provider === 'simulated') {
      response.status(200).json({
        texto: '',
        simulada: true,
        mensaje: 'ASR en modo demostración: no se transcribió el audio.'
      });
      return;
    }

    if (provider === 'quechua') {
      const resultado = await transcribeQuechua(audio);
      response.status(200).json({
        texto: resultado.text,
        simulada: false,
        clasificada: true,
        intencion: resultado.intent,
        confianzaIntencion: resultado.confidence
      });
      return;
    }

    const texto = provider === 'google'
      ? await transcribeGoogle(audio)
      : await transcribeWhisper(audio);
    response.status(200).json({ texto, simulada: false });
  } catch (error) {
    const message = error instanceof Error ? error.message : 'Cloud ASR request failed.';
    response.status(500).json({
      error: { code: 'asr_provider_failed', message: message.startsWith('ASR') ? message : 'The configured ASR provider failed.' }
    });
  }
});

function extractMultipartAudio(body: Buffer, boundary: string): { bytes: Buffer; filename: string; mime: string } | null {
  const marker = Buffer.from(`--${boundary}`);
  let partStart = body.indexOf(marker);
  while (partStart >= 0) {
    const headerEnd = body.indexOf(Buffer.from('\r\n\r\n'), partStart);
    if (headerEnd < 0) break;
    const nextMarker = body.indexOf(Buffer.concat([Buffer.from('\r\n'), marker]), headerEnd + 4);
    const end = nextMarker >= 0 ? nextMarker : body.length;
    const headers = body.subarray(partStart + marker.length + 2, headerEnd).toString('utf8');
    const nameMatch = /name="([^"]+)"/i.exec(headers);
    if (nameMatch?.[1] === 'audio') {
      const filenameMatch = /filename="([^"]*)"/i.exec(headers);
      const mimeMatch = /^Content-Type:\s*(.+)$/im.exec(headers);
      return {
        bytes: body.subarray(headerEnd + 4, end),
        filename: filenameMatch?.[1] || 'audio.wav',
        mime: mimeMatch?.[1]?.trim() || 'application/octet-stream'
      };
    }
    partStart = nextMarker >= 0 ? body.indexOf(marker, nextMarker) : -1;
  }
  return null;
}

async function transcribeGoogle(audio: { bytes: Buffer; mime: string }): Promise<string> {
  if (!env.googleAsrApiKey) throw new Error('ASR Google API key is missing.');
  const response = await fetch(
    `https://speech.googleapis.com/v1/speech:recognize?key=${encodeURIComponent(env.googleAsrApiKey)}`,
    {
      method: 'POST',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify({
        config: { languageCode: env.googleAsrLanguage, encoding: 'LINEAR16', sampleRateHertz: 16000 },
        audio: { content: audio.bytes.toString('base64') }
      })
    }
  );
  if (!response.ok) throw new Error('ASR provider request failed.');
  const payload = await response.json() as { results?: Array<{ alternatives?: Array<{ transcript?: string }> }> };
  return payload.results?.flatMap((result) => result.alternatives ?? [])
    .map((alternative) => alternative.transcript?.trim())
    .filter((text): text is string => !!text)
    .join(' ') || '';
}

async function transcribeWhisper(audio: { bytes: Buffer; filename: string; mime: string }): Promise<string> {
  if (!env.openaiAsrApiKey) throw new Error('ASR Whisper API key is missing.');
  const form = new FormData();
  form.set('model', 'whisper-1');
  form.set('language', 'es');
  const blobBytes = new Uint8Array(audio.bytes.byteLength);
  blobBytes.set(audio.bytes);
  form.set('file', new Blob([blobBytes], { type: audio.mime }), audio.filename);
  const response = await fetch(`${env.openaiAsrBaseUrl.replace(/\/$/, '')}/audio/transcriptions`, {
    method: 'POST',
    headers: { authorization: `Bearer ${env.openaiAsrApiKey}` },
    body: form
  });
  if (!response.ok) throw new Error('ASR provider request failed.');
  const payload = await response.json() as { text?: string };
  return typeof payload.text === 'string' ? payload.text : '';
}

const quechuaIntentLabels = new Set([
  'registrar_venta',
  'consultar_stock',
  'consultar_ventas',
  'agregar_producto',
  'consulta_cognitiva',
  'ayuda'
]);

async function transcribeQuechua(
  audio: { bytes: Buffer; filename: string; mime: string }
): Promise<{ text: string; intent: string | null; confidence: number | null }> {
  const fileBytes = new Uint8Array(audio.bytes.byteLength);
  fileBytes.set(audio.bytes);
  const form = new FormData();
  form.set('audio', new Blob([fileBytes], { type: audio.mime }), audio.filename);
  const response = await fetch(env.quechuaAsrApiUrl, {
    method: 'POST',
    headers: env.quechuaAsrApiToken
      ? { authorization: `Bearer ${env.quechuaAsrApiToken}` }
      : undefined,
    body: form,
    signal: AbortSignal.timeout(120_000)
  });
  if (!response.ok) throw new Error('ASR Quechua provider request failed.');
  const payload = await response.json() as {
    text?: unknown;
    intent?: unknown;
    confidence?: unknown;
  };
  if (typeof payload.text !== 'string') {
    throw new Error('ASR Quechua provider returned an invalid response.');
  }
  const intent = typeof payload.intent === 'string' && quechuaIntentLabels.has(payload.intent)
    ? payload.intent
    : null;
  const confidence = typeof payload.confidence === 'number' &&
    Number.isFinite(payload.confidence) &&
    payload.confidence >= 0 &&
    payload.confidence <= 1
    ? payload.confidence
    : null;
  return {
    text: payload.text.trim(),
    intent: intent && confidence !== null ? intent : null,
    confidence: intent && confidence !== null ? confidence : null
  };
}
