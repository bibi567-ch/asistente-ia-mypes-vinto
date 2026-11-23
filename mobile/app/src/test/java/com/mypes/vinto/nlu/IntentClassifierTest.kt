package com.mypes.vinto.nlu

import org.junit.Assert.assertEquals
import org.junit.Test

class IntentClassifierTest {
    private val classifier = IntentClassifier()

    @Test
    fun `recognizes requested phone intents`() {
        assertEquals(IntentClassifier.Intencion.LLAMAR_CONTACTO, classifier.clasificar("llamar a mamá"))
        assertEquals(IntentClassifier.Intencion.ENVIAR_MENSAJE, classifier.clasificar("envía mensaje a Juan"))
        assertEquals(IntentClassifier.Intencion.LEER_TEXTO, classifier.clasificar("lee este texto"))
        assertEquals(IntentClassifier.Intencion.CREAR_ALARMA, classifier.clasificar("pon una alarma"))
        assertEquals(IntentClassifier.Intencion.ABRIR_APP, classifier.clasificar("abre WhatsApp"))
        assertEquals(IntentClassifier.Intencion.CONSULTAR_HORA, classifier.clasificar("qué hora es"))
        assertEquals(IntentClassifier.Intencion.LINTERNA, classifier.clasificar("enciende la linterna"))
        assertEquals(IntentClassifier.Intencion.AJUSTES_RED, classifier.clasificar("abrir ajustes de red"))
        assertEquals(IntentClassifier.Intencion.GUIA_TELEFONO, classifier.clasificar("guía del teléfono"))
        assertEquals(IntentClassifier.Intencion.AYUDA, classifier.clasificar("qué puedo hacer"))
        assertEquals(IntentClassifier.Intencion.DESCONOCIDO, classifier.clasificar("cancelar venta"))
    }

    @Test
    fun `cloud fallback is limited to online requests below seventy percent confidence`() {
        assertEquals(true, CloudAsrFallback.shouldFallback(true, 0.69f))
        assertEquals(false, CloudAsrFallback.shouldFallback(true, 0.70f))
        assertEquals(false, CloudAsrFallback.shouldFallback(false, 0.20f))
        assertEquals(false, CloudAsrFallback.shouldFallback(true, Float.NaN))
    }
}
