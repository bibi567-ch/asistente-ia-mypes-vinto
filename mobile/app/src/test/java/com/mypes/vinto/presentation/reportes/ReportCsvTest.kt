package com.mypes.vinto.presentation.reportes

import com.mypes.vinto.data.Venta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportCsvTest {
    @Test
    fun `exporta cabecera y venta en formato CSV con separador seguro`() {
        val contenido = crearContenidoCsv(
            listOf(
                Venta(
                    id = "venta-1",
                    total = 12.5,
                    formaPago = "FIADO",
                    clienteFiado = "Ana; \"M\"",
                    creadoEn = 0L
                )
            )
        )

        assertTrue(contenido.startsWith("\uFEFF"))
        assertTrue(contenido.contains("\"Fecha\";\"Total Bs\";\"Forma de pago\";\"Cliente\""))
        assertTrue(contenido.contains("\"12.50\";\"FIADO\";\"Ana; \"\"M\"\"\""))
    }

    @Test
    fun `exporta solo la cabecera cuando no hay ventas`() {
        val contenido = crearContenidoCsv(emptyList())
        assertEquals(1, contenido.trimEnd().lineSequence().count())
        assertTrue(contenido.contains("\"Fecha\";\"Total Bs\";\"Forma de pago\";\"Cliente\""))
    }
}
