package com.mypes.vinto.nlu

import org.junit.Assert.assertEquals
import org.junit.Test

class EntityExtractorTest {
    private val extractor = EntityExtractor()

    @Test
    fun `extrae cantidad y producto de frase con modismo local`() {
        val resultado = extractor.extraer("Vendí dos cuartillas de arroz a cincuenta bolivianos")

        assertEquals(2.0, resultado.cantidad!!, 0.0)
        assertEquals("arroz", resultado.producto)
        assertEquals(50.0, resultado.monto!!, 0.0)
        assertEquals("cuartilla", resultado.unidad)
    }

    @Test
    fun `detecta forma de pago al fiado y extrae cliente`() {
        val resultado = extractor.extraer("Anota 3 panes al fiado para doña Maria")

        assertEquals("panes", resultado.producto)
        assertEquals("FIADO", resultado.formaPago)
        assertEquals("Maria", resultado.cliente)
    }
}
