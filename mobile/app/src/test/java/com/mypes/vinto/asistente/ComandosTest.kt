package com.mypes.vinto.asistente

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Mismos casos que frontend/src/lib/nlu/nlu.test.ts: la app y la web entienden igual. */
class ComandosTest {

    @Test
    fun clasificaLasOrdenesDelPerfil() {
        val casos = mapOf(
            "llama a mi hermana" to Comando.LLAMAR_CONTACTO,
            "manda mensaje a Rosa que diga ya llegué" to Comando.ENVIAR_MENSAJE,
            "pon una alarma a las seis" to Comando.CREAR_ALARMA,
            "abre whatsapp" to Comando.ABRIR_APP,
            "qué hora es" to Comando.CONSULTAR_HORA,
            "prende la linterna" to Comando.LINTERNA,
            "activa el wifi" to Comando.AJUSTES_RED,
            "guía para compartir ubicación" to Comando.GUIA_TELEFONO,
            "ayuda" to Comando.AYUDA,
            "kichay whatsapp" to Comando.ABRIR_APP,
            "wawqiyta llamay" to Comando.LLAMAR_CONTACTO,
            "hola" to Comando.DESCONOCIDO
        )
        casos.forEach { (texto, esperado) -> assertEquals(texto, esperado, ClasificadorComandos.clasificar(texto)) }
    }

    @Test
    fun respetaElOrdenDeLasReglas() {
        assertEquals(Comando.LLAMAR_CONTACTO, ClasificadorComandos.clasificar("llama y abre"))
    }

    @Test
    fun pideConfirmacionSoloEnAccionesQueCambianAlgo() {
        assertTrue(ClasificadorComandos.requiereConfirmacion(Comando.LLAMAR_CONTACTO))
        assertFalse(ClasificadorComandos.requiereConfirmacion(Comando.CONSULTAR_HORA))
        assertFalse(ClasificadorComandos.requiereConfirmacion(Comando.GUIA_TELEFONO))
    }

    @Test
    fun extraeNumeros() {
        assertEquals("71234567", ExtractorComandos.extraer("llama al 71234567").numeroTelefono)
        assertEquals(
            "71234567",
            ExtractorComandos.extraer("llama al siete uno dos tres cuatro cinco seis siete").numeroTelefono
        )
    }

    @Test
    fun extraeContactoYMensaje() {
        assertEquals("mi hermana", ExtractorComandos.extraer("llama a mi hermana").contacto)
        assertEquals("ya llegué", ExtractorComandos.extraer("manda mensaje a Rosa que diga ya llegué").mensaje)
    }

    @Test
    fun extraeHoras() {
        val media = ExtractorComandos.extraer("pon una alarma a las seis y media")
        assertEquals(6, media.hora)
        assertEquals(30, media.minutos)
        assertEquals(19, ExtractorComandos.extraer("alarma a las 7 de la noche").hora)
        val puntos = ExtractorComandos.extraer("alarma 6:45")
        assertEquals(6, puntos.hora)
        assertEquals(45, puntos.minutos)
    }

    @Test
    fun extraeAplicacionYLinterna() {
        assertEquals("whatsapp", ExtractorComandos.extraer("abre whatsapp").nombreApp)
        assertFalse(ExtractorComandos.extraer("apaga la linterna").linternaEncendida)
        assertTrue(ExtractorComandos.extraer("prende la linterna").linternaEncendida)
    }

    @Test
    fun encuentraGuias() {
        assertEquals("ubicacion", Guias.buscar("guía para compartir ubicación")?.id)
        assertEquals(8, Guias.TODAS.size)
    }
}
