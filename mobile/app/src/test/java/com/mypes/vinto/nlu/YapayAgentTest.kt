package com.mypes.vinto.nlu

import org.junit.Assert.assertTrue
import org.junit.Test
import kotlinx.coroutines.runBlocking

class YapayAgentTest {
    @Test
    fun `adding a product requests confirmation before invoking storage`() {
        var inserts = 0
        val agent = YapayAgent(
            YapayAgent.Tools(
                insertarProducto = { inserts++; true },
                totalVentasDesde = { 0.0 }
            )
        )

        assertTrue(agent.systemPrompt.contains("quechua, castellano o aymara"))
        val confirmationRequest = runBlocking {
            agent.procesarComando("agrega harina por 12.50 Bs con stock 8")
        }
        assertTrue(confirmationRequest.contains("¿Confirmas"))
        assertTrue(inserts == 0)

        val saved = runBlocking { agent.procesarComando("sí") }
        assertTrue(saved.contains("guardado"))
        assertTrue(inserts == 1)
    }
}
