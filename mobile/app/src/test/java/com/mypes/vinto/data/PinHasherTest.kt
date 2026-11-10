package com.mypes.vinto.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PinHasherTest {
    @Test
    fun `hash de PIN es seguro y verificable`() {
        val hash = PinHasher.crear("1234")

        assertTrue(PinHasher.verificar("1234", hash))
        assertFalse(PinHasher.verificar("4321", hash))
        assertFalse(hash.hash.contains("1234"))
    }

    @Test
    fun `cada PIN recibe una sal aleatoria`() {
        val primero = PinHasher.crear("1234")
        val segundo = PinHasher.crear("1234")

        assertNotEquals(primero.salt, segundo.salt)
        assertNotEquals(primero.hash, segundo.hash)
        assertEquals(120_000, primero.iteraciones)
    }
}
