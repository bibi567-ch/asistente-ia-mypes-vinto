package com.mypes.vinto.nlu

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import org.json.JSONArray
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import java.io.ByteArrayOutputStream
import java.io.Closeable
import java.io.File
import java.io.IOException

class AsrEngine(private val context: Context) : Closeable {
    interface Resultado {
        fun onTextoParcial(texto: String)
        fun onTextoFinal(texto: String, confianza: Float, audioPcm: ByteArray)
        fun onError(mensaje: String)
    }

    companion object {
        private const val SAMPLE_RATE = 16_000
        private const val GRAMMAR_ASSET = "vosk-grammar.json"

        fun extraerConfianza(json: String): Float = runCatching {
            val root = JSONObject(json)
            val alternativas = root.optJSONArray("result") ?: return@runCatching 0f
            val confidencias = (0 until alternativas.length())
                .mapNotNull { alternativas.optJSONObject(it)?.takeIf { item -> item.has("conf") }?.optDouble("conf") }
            confidencias.average().takeIf { !it.isNaN() }?.toFloat() ?: 0f
        }.getOrDefault(0f)
    }

    private var model: Model? = null
    private var grammarJson: String? = null
    @Volatile private var escuchando = false
    @Volatile private var audioRecord: AudioRecord? = null
    @Volatile private var listenerThread: Thread? = null

    fun inicializar(): Boolean = runCatching {
        val grammar = context.assets.open(GRAMMAR_ASSET).bufferedReader().use { it.readText() }
        val phrases = JSONArray(grammar)
        require(phrases.length() > 0) { "La gramática Vosk está vacía" }
        grammarJson = phrases.toString()
        model = Model(copiarModeloDesdeAssets())
    }.isSuccess

    fun escuchar(resultado: Resultado) {
        val modelo = model
        if (modelo == null) {
            resultado.onError("Modelo no inicializado")
            return
        }
        val grammar = grammarJson
        if (grammar.isNullOrBlank()) {
            resultado.onError("La gramática Vosk no está inicializada")
            return
        }
        detener()
        esperarFinDeEscucha()
        val minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        if (minBufferSize <= 0) {
            resultado.onError("No se pudo configurar la grabación de audio")
            return
        }

        var nuevaGrabadora: AudioRecord? = null
        var nuevoReconocedor: Recognizer? = null
        try {
            val recorder = AudioRecord(
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minBufferSize * 2
            )
            nuevaGrabadora = recorder
            if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                recorder.release()
                nuevaGrabadora = null
                resultado.onError("No se pudo inicializar el micrófono")
                return
            }
            val recognizer = Recognizer(modelo, SAMPLE_RATE.toFloat(), grammar)
            nuevoReconocedor = recognizer
            recorder.startRecording()
            audioRecord = recorder
            escuchando = true
            listenerThread = Thread(
                { reconocer(recorder, recognizer, minBufferSize * 2, resultado) },
                "vinto-vosk"
            ).apply { start() }
        } catch (error: SecurityException) {
            nuevoReconocedor?.close()
            nuevaGrabadora?.release()
            resultado.onError(error.message ?: "Permiso de micrófono denegado")
        } catch (error: IllegalStateException) {
            nuevoReconocedor?.close()
            nuevaGrabadora?.release()
            resultado.onError(error.message ?: "No se pudo iniciar el micrófono")
        } catch (error: IOException) {
            nuevoReconocedor?.close()
            nuevaGrabadora?.release()
            resultado.onError(error.message ?: "No se pudo inicializar Vosk")
        } catch (error: IllegalArgumentException) {
            nuevoReconocedor?.close()
            nuevaGrabadora?.release()
            resultado.onError(error.message ?: "La configuración de audio no es válida")
        }
    }

    fun detener() {
        escuchando = false
        detenerGrabadora(audioRecord)
        listenerThread?.interrupt()
    }

    override fun close() {
        detener()
        esperarFinDeEscucha()
        model?.close()
        model = null
    }

    private fun reconocer(
        recorder: AudioRecord,
        recognizer: Recognizer,
        bufferSize: Int,
        resultado: Resultado
    ) {
        val buffer = ByteArray(bufferSize)
        val audioUtterance = ByteArrayOutputStream()
        var ultimoTextoFinal: String? = null
        try {
            while (escuchando && !Thread.currentThread().isInterrupted) {
                val read = recorder.read(buffer, 0, buffer.size, AudioRecord.READ_BLOCKING)
                if (read < 0) {
                    if (escuchando) resultado.onError("Falló la lectura del micrófono")
                    break
                }
                if (read == 0) continue
                audioUtterance.write(buffer, 0, read)
                if (recognizer.acceptWaveForm(buffer, read)) {
                    val json = recognizer.result
                    val texto = extraerTexto(json)
                    if (!texto.isNullOrBlank() && texto != ultimoTextoFinal) {
                        ultimoTextoFinal = texto
                        resultado.onTextoFinal(texto, extraerConfianza(json), audioUtterance.toByteArray())
                        escuchando = false
                        break
                    }
                    audioUtterance.reset()
                } else {
                    extraerTexto(recognizer.partialResult)?.let(resultado::onTextoParcial)
                }
            }
            val finalJson = recognizer.finalResult
            val textoFinal = extraerTexto(finalJson)
            if (!textoFinal.isNullOrBlank() && textoFinal != ultimoTextoFinal) {
                resultado.onTextoFinal(textoFinal, extraerConfianza(finalJson), audioUtterance.toByteArray())
            }
        } catch (error: RuntimeException) {
            if (escuchando) resultado.onError(error.message ?: "Error de reconocimiento")
        } finally {
            escuchando = false
            detenerGrabadora(recorder)
            recorder.release()
            audioRecord = null
            recognizer.close()
            listenerThread = null
        }
    }

    private fun detenerGrabadora(recorder: AudioRecord?) {
        if (recorder == null) return
        synchronized(recorder) {
            if (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) recorder.stop()
        }
    }

    private fun esperarFinDeEscucha() {
        val thread = listenerThread
        if (thread != null && thread !== Thread.currentThread()) {
            try {
                thread.join()
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }
    }

    private fun extraerTexto(json: String): String? = runCatching {
        JSONObject(json).let { resultado ->
            resultado.optString("text").ifBlank { resultado.optString("partial") }.trim()
        }.takeIf(String::isNotBlank)
    }.getOrNull()

    private fun copiarModeloDesdeAssets(): String {
        val nombreModelo = "vosk-model-small-es-0.42"
        val destino = File(context.filesDir, nombreModelo)
        val requiredFiles = listOf("am/final.mdl", "conf/model.conf")
        fun modeloCompleto(): Boolean = requiredFiles.all { relativePath ->
            File(destino, relativePath).let { it.isFile && it.length() > 0L }
        }
        if (!modeloCompleto()) {
            if (destino.exists() && !destino.deleteRecursively()) {
                throw IOException("No se pudo reemplazar la copia incompleta del modelo Vosk")
            }
            copiarCarpetaAsset(nombreModelo, destino)
        }
        if (!modeloCompleto()) {
            throw IOException("Los archivos requeridos del modelo Vosk no están disponibles")
        }
        return destino.absolutePath
    }

    private fun copiarCarpetaAsset(rutaAsset: String, destino: File) {
        val hijos = context.assets.list(rutaAsset).orEmpty()
        if (hijos.isEmpty()) {
            destino.parentFile?.mkdirs()
            context.assets.open(rutaAsset).use { input ->
                destino.outputStream().use(input::copyTo)
            }
            return
        }

        destino.mkdirs()
        hijos.forEach { hijo ->
            copiarCarpetaAsset("$rutaAsset/$hijo", File(destino, hijo))
        }
    }
}
