package com.mypes.vinto.data

import org.junit.Assert.*
import org.junit.Test

class PinHasherTest {

    @Test
    fun pinCorrectoValida() {
        val guardado = PinHasher.crear("1234", iteraciones = 1000)
        assertTrue(PinHasher.verificar("1234", guardado))
    }

    @Test
    fun pinIncorrectoNoValida() {
        val guardado = PinHasher.crear("1234", iteraciones = 1000)
        assertFalse(PinHasher.verificar("4321", guardado))
    }

    @Test
    fun elHashNoContieneElPin() {
        val guardado = PinHasher.crear("1234", iteraciones = 1000)
        assertFalse(guardado.hash.contains("1234"))
    }

    @Test
    fun mismoPinGeneraHashDistintoPorLaSal() {
        val a = PinHasher.crear("1234", iteraciones = 1000)
        val b = PinHasher.crear("1234", iteraciones = 1000)
        assertNotEquals(a.salt, b.salt)
        assertNotEquals(a.hash, b.hash)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rechazaPinInvalido() {
        PinHasher.crear("12ab")
    }
}
