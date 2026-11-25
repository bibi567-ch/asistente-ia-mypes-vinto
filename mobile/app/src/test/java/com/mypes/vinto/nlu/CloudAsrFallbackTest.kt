package com.mypes.vinto.nlu

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CloudAsrFallbackTest {
    @Test
    fun `falls back only online and below seventy percent`() {
        assertTrue(CloudAsrFallback.shouldFallback(isOnline = true, confidence = 0.69f))
        assertFalse(CloudAsrFallback.shouldFallback(isOnline = false, confidence = 0.69f))
        assertFalse(CloudAsrFallback.shouldFallback(isOnline = true, confidence = 0.70f))
        assertTrue(CloudAsrFallback.shouldUseCloud(isOnline = true, confidence = 0.95f, forceCloud = true))
        assertFalse(CloudAsrFallback.shouldUseCloud(isOnline = false, confidence = 0.95f, forceCloud = true))
    }

    @Test
    fun `ignores simulated or empty cloud transcripts`() {
        assertEquals(null, CloudAsrResponse("demo", simulada = true).textoUtilizable())
        assertEquals(null, CloudAsrResponse("  ").textoUtilizable())
        assertEquals("Allin p'unchay", CloudAsrResponse(" Allin p'unchay ").textoUtilizable())
    }
}
