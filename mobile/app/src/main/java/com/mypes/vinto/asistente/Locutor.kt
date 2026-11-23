package com.mypes.vinto.asistente

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/** Respuestas habladas con el motor de voz del teléfono (funciona sin internet si la voz está instalada). */
class Locutor(context: Context) : TextToSpeech.OnInitListener, AutoCloseable {
    private val tts = TextToSpeech(context.applicationContext, this)
    private var listo = false

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) return
        val idiomas = listOf(Locale("es", "BO"), Locale("es", "US"), Locale("es", "ES"), Locale("es"))
        listo = idiomas.any { idioma ->
            val r = tts.setLanguage(idioma)
            r != TextToSpeech.LANG_MISSING_DATA && r != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun hablar(texto: String, velocidad: Float = 0.9f) {
        if (!listo || texto.isBlank()) return
        tts.setSpeechRate(velocidad)
        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "iavinto-${System.currentTimeMillis()}")
    }

    fun callar() {
        if (listo) tts.stop()
    }

    override fun close() {
        tts.stop()
        tts.shutdown()
    }
}
