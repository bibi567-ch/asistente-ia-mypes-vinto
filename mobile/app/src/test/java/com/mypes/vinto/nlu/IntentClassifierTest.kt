package com.mypes.vinto.nlu

import org.junit.Assert.assertEquals
import org.junit.Test

class IntentClassifierTest {
    private val classifier = IntentClassifier()

    @Test
    fun `clasifica intenciones del asistente por frases y palabras clave`() {
        assertEquals(
            IntentClassifier.Intencion.LLAMAR_CONTACTO,
            classifier.clasificar("Llama al 70000000")
        )
        assertEquals(IntentClassifier.Intencion.CONSULTA_COGNITIVA, classifier.clasificar("¿Qué es el IVA?"))
        assertEquals(IntentClassifier.Intencion.CONSULTA_COGNITIVA, classifier.clasificar("¿Cómo calculo mi ganancia?"))
        assertEquals(IntentClassifier.Intencion.AYUDA, classifier.clasificar("comando no reconocido"))
        assertEquals(11, IntentClassifier.Intencion.entries.size)
    }
}
