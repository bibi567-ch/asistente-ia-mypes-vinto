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

    @Test
    fun `product command with explicit product word still requests confirmation`() {
        val agent = YapayAgent(
            YapayAgent.Tools(
                insertarProducto = { true },
                totalVentasDesde = { 0.0 }
            )
        )

        assertTrue(agent.puedePrepararProducto("agrega producto harina por 12.50 Bs"))
        val response = runBlocking {
            agent.procesarComando("agrega producto harina por 12.50 Bs")
        }

        assertTrue(response.contains("¿Confirmas"))
    }

    @Test
    fun `remote sales intent can answer a non Spanish transcription`() {
        val agent = YapayAgent(
            YapayAgent.Tools(
                insertarProducto = { true },
                totalVentasDesde = { 25.0 }
            )
        )

        val response = runBlocking {
            agent.procesarComando(
                "qhawariy",
                IntentClassifier.Intencion.CONSULTAR_VENTAS
            )
        }

        assertTrue(response.contains("ventas registradas hoy suman"))
        assertTrue(response.contains("25"))
    }
}
