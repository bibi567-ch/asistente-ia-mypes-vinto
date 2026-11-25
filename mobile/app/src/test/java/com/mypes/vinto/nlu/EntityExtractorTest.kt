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
    fun `extrae entidades del comando de venta breve`() {
        val resultado = extractor.extraer("Vender dos papas")

        assertEquals(2.0, resultado.cantidad!!, 0.0)
        assertEquals("papas", resultado.producto)
    }

    @Test
    fun `detecta forma de pago al fiado y extrae cliente`() {
        val resultado = extractor.extraer("Anota 3 panes al fiado para doña Maria")

        assertEquals("panes", resultado.producto)
        assertEquals("FIADO", resultado.formaPago)
        assertEquals("Maria", resultado.cliente)
    }

    @Test
    fun `extrae numeros quechuas y monto marcado con qullqi`() {
        val resultado = extractor.extraer("qhatuy iskay papa a phichqa qullqi")

        assertEquals(2.0, resultado.cantidad!!, 0.0)
        assertEquals("papa", resultado.producto)
        assertEquals(5.0, resultado.monto!!, 0.0)
    }

    @Test
    fun `reconoce algunos numeros quechuas compuestos`() {
        assertEquals(11.0, extractor.extraer("chunka jukniyuq papa").cantidad!!, 0.0)
        assertEquals(20.0, extractor.extraer("iskay chunka papas").cantidad!!, 0.0)
    }

    @Test
    fun `extrae PIN de palabras separadas y puntuacion`() {
        assertEquals("1234", extractor.extraerPin("Mi PIN es uno, dos, tres y cuatro"))
        assertEquals("0123", extractor.extraerPin("cero uno dos tres"))
    }

    @Test
    fun `rechaza PIN hablado incompleto o ambiguo`() {
        assertEquals(null, extractor.extraerPin("uno dos tres"))
        assertEquals(null, extractor.extraerPin("mi pin es doce treinta y cuatro"))
    }
}
