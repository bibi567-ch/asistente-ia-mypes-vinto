package com.mypes.vinto.data

import org.junit.Assert.*
import org.junit.Test

class PoliticaAccesoTest {
    private val t0 = 1_000_000L

    @Test
    fun dosFallosNoBloquean() {
        var e = EstadoAcceso()
        e = PoliticaAcceso.registrarFallo(e, t0)
        e = PoliticaAcceso.registrarFallo(e, t0)
        assertFalse(PoliticaAcceso.estaBloqueado(e, t0))
        assertEquals(2, e.intentosFallidos)
    }

    @Test
    fun tercerFalloBloquea30Segundos() {
        var e = EstadoAcceso()
        repeat(3) { e = PoliticaAcceso.registrarFallo(e, t0) }
        assertTrue(PoliticaAcceso.estaBloqueado(e, t0 + 1_000))
        assertEquals(30L, PoliticaAcceso.segundosRestantes(e, t0))
    }

    @Test
    fun despuesDe30SegundosSeDesbloquea() {
        var e = EstadoAcceso()
        repeat(3) { e = PoliticaAcceso.registrarFallo(e, t0) }
        assertFalse(PoliticaAcceso.estaBloqueado(e, t0 + 30_000))
    }

    @Test
    fun fallosDuranteElBloqueoNoCambianElEstado() {
        var e = EstadoAcceso()
        repeat(3) { e = PoliticaAcceso.registrarFallo(e, t0) }
        val igual = PoliticaAcceso.registrarFallo(e, t0 + 5_000)
        assertEquals(e, igual)
    }

    @Test
    fun exitoReiniciaLosIntentos() {
        var e = EstadoAcceso()
        e = PoliticaAcceso.registrarFallo(e, t0)
        e = PoliticaAcceso.registrarFallo(e, t0)
        assertEquals(EstadoAcceso(), PoliticaAcceso.registrarExito())
    }
}
