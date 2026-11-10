package com.mypes.vinto.nlu

import org.junit.Assert.assertEquals
import org.junit.Test

class IntentClassifierTest {
    private val classifier = IntentClassifier()

    @Test
    fun `clasifica una venta dictada con conjugacion local`() {
        assertEquals(
            IntentClassifier.Intencion.VENTA,
            classifier.clasificar("Vendí dos cuartillas de arroz")
        )
    }
}
