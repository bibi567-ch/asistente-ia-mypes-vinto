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
        assertEquals(IntentClassifier.Intencion.REGISTRAR_VENTA, classifier.clasificar("Quiero vender un producto"))
        assertEquals(IntentClassifier.Intencion.CONSULTAR_STOCK, classifier.clasificar("¿Cuánto queda de harina?"))
        assertEquals(IntentClassifier.Intencion.CONSULTAR_VENTAS, classifier.clasificar("¿Cuánto vendí hoy?"))
        assertEquals(IntentClassifier.Intencion.AGREGAR_PRODUCTO, classifier.clasificar("Agrega producto"))
        assertEquals(IntentClassifier.Intencion.AYUDA, classifier.clasificar("comando no reconocido"))
        assertEquals(15, IntentClassifier.Intencion.entries.size)
    }

    @Test
    fun `acepta una intencion remota solo sobre el umbral de confianza`() {
        assertEquals(
            IntentClassifier.Intencion.REGISTRAR_VENTA,
            classifier.clasificarResultadoRemoto("registrar_venta", 0.81f)
        )
        assertEquals(null, classifier.clasificarResultadoRemoto("registrar_venta", 0.61f))
        assertEquals(null, classifier.clasificarResultadoRemoto("unknown", 0.99f))
        assertEquals(null, classifier.clasificarResultadoRemoto("consultar_stock", Float.NaN))
    }
}
