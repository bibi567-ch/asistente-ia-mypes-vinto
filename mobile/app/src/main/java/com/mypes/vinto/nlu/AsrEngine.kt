package com.mypes.vinto.nlu

import android.content.Context
import org.json.JSONObject
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import java.io.Closeable
import java.io.File

class AsrEngine(private val context: Context) : Closeable {
    interface Resultado {
        fun onTextoParcial(texto: String)
        fun onTextoFinal(texto: String)
        fun onError(mensaje: String)
    }

    private var model: Model? = null
    private var speechService: SpeechService? = null

    fun inicializar(): Boolean = runCatching {
        model = Model(copiarModeloDesdeAssets())
    }.isSuccess

    fun escuchar(resultado: Resultado) {
        val modelo = model
        if (modelo == null) {
            resultado.onError("Modelo no inicializado")
            return
        }

        runCatching {
            detener()
            val recognizer = Recognizer(modelo, 16_000.0f)
            speechService = SpeechService(recognizer, 16_000.0f).also { service ->
                service.startListening(object : RecognitionListener {
                    override fun onPartialResult(hipotesis: String) {
                        extraerTexto(hipotesis)?.let(resultado::onTextoParcial)
                    }

                    override fun onResult(hipotesis: String) {
                        extraerTexto(hipotesis)?.let(resultado::onTextoFinal)
                    }

                    override fun onFinalResult(hipotesis: String) {
                        extraerTexto(hipotesis)?.let(resultado::onTextoFinal)
                    }

                    override fun onError(error: Exception) {
                        resultado.onError(error.message ?: "Error de reconocimiento")
                    }

                    override fun onTimeout() {
                        resultado.onError("Tiempo de escucha agotado")
                    }
                })
            }
        }.onFailure { error ->
            resultado.onError(error.message ?: "Error al iniciar el micrófono")
        }
    }

    fun detener() {
        speechService?.stop()
        speechService = null
    }

    override fun close() {
        detener()
        model?.close()
        model = null
    }

    private fun extraerTexto(json: String): String? = runCatching {
        JSONObject(json).let { objeto ->
            objeto.optString("text").ifBlank { objeto.optString("partial") }.trim()
        }.takeIf(String::isNotBlank)
    }.getOrNull()

    private fun copiarModeloDesdeAssets(): String {
        val nombreModelo = "vosk-model-small-es-0.42"
        val destino = File(context.filesDir, nombreModelo)
        if (!destino.exists()) copiarCarpetaAsset(nombreModelo, destino)
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
