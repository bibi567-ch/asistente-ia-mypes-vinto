package com.mypes.vinto.presentation.chat

import com.mypes.vinto.nlu.EntityExtractor
import com.mypes.vinto.nlu.AsistenteVozFacade
import com.mypes.vinto.nlu.IntentClassifier
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatCommandRouterTest {
    private val classifier = IntentClassifier()
    private val extractor = EntityExtractor()

    @Test
    fun `sale command routes with product and quantity for review`() {
        val texto = "Quiero vender 2 papas"

        val destino = resolverDestinoChat(
            texto = texto,
            intencion = classifier.clasificar(texto),
            entidades = extractor.extraer(texto),
            puedePrepararProducto = false
        )

        assertEquals(ChatDestination.Venta("papas", 2.0), destino)
    }

    @Test
    fun `stock command opens inventory with extracted filter`() {
        val texto = "stock de papa"

        val destino = resolverDestinoChat(
            texto = texto,
            intencion = classifier.clasificar(texto),
            entidades = extractor.extraer(texto),
            puedePrepararProducto = false
        )

        assertEquals(ChatDestination.Inventario("papa"), destino)
    }

    @Test
    fun `incomplete product request opens filtered inventory`() {
        val texto = "agrega producto harina"

        val destino = resolverDestinoChat(
            texto = texto,
            intencion = classifier.clasificar(texto),
            entidades = extractor.extraer(texto),
            puedePrepararProducto = false
        )

        assertEquals(ChatDestination.Inventario("harina"), destino)
    }

    @Test
    fun `complete product request stays with the agent for confirmation`() {
        val texto = "agrega producto harina por 12.50 Bs"

        val destino = resolverDestinoChat(
            texto = texto,
            intencion = classifier.clasificar(texto),
            entidades = extractor.extraer(texto),
            puedePrepararProducto = true
        )

        assertEquals(null, destino)
    }

    @Test
    fun `debt query opens accounts receivable`() {
        val texto = "Mostrar mis deudas"

        val destino = resolverDestinoChat(
            texto = texto,
            intencion = classifier.clasificar(texto),
            entidades = extractor.extraer(texto),
            puedePrepararProducto = false
        )

        assertEquals(ChatDestination.Fiados, destino)
    }

    @Test
    fun `unclassified message without a local destination can use the remote guide`() {
        assertTrue(
            debeConsultarGuiaRemota(
                intencion = IntentClassifier.Intencion.DESCONOCIDA,
                destino = null,
                esperandoConfirmacionProducto = false
            )
        )
    }

    @Test
    fun `classified cognitive question can use the remote guide`() {
        assertTrue(
            debeConsultarGuiaRemota(
                intencion = IntentClassifier.Intencion.CONSULTA_COGNITIVA,
                destino = null,
                esperandoConfirmacionProducto = false
            )
        )
    }

    @Test
    fun `commercial navigation is not sent to the remote guide`() {
        assertFalse(
            debeConsultarGuiaRemota(
                intencion = IntentClassifier.Intencion.REGISTRAR_VENTA,
                destino = ChatDestination.Venta("papas", 2.0),
                esperandoConfirmacionProducto = false
            )
        )
    }

    @Test
    fun `pending product confirmation stays local`() {
        assertFalse(
            debeConsultarGuiaRemota(
                intencion = IntentClassifier.Intencion.DESCONOCIDA,
                destino = null,
                esperandoConfirmacionProducto = true
            )
        )
    }

    @Test
    fun `isolated filler is rejected and not shown as a transcript`() {
        val result = resultadoVoz(texto = "de")

        assertNotNull(errorTranscripcionParaChat(result))
        assertFalse(mostrarTranscripcionEnChat(result))
    }

    @Test
    fun `low confidence voice transcript is rejected before guide routing`() {
        val result = resultadoVoz(texto = "quizá vender papas", confianza = 0.4f)

        assertNotNull(errorTranscripcionParaChat(result))
        assertTrue(mostrarTranscripcionEnChat(result))
    }

    @Test
    fun `quechua audio without cloud transcript is rejected`() {
        val result = resultadoVoz(requiereNube = true)

        assertNotNull(errorTranscripcionParaChat(result))
        assertFalse(mostrarTranscripcionEnChat(result))
    }

    @Test
    fun `cloud transcript without remote intent is not run through local rules`() {
        val result = resultadoVoz(
            transcripcionNube = true,
            requiereNube = true
        )

        assertNotNull(errorTranscripcionParaChat(result))
        assertTrue(mostrarTranscripcionEnChat(result))
    }

    @Test
    fun `recognized high confidence voice result is accepted`() {
        val result = resultadoVoz(
            intencionRemota = IntentClassifier.Intencion.REGISTRAR_VENTA,
            transcripcionNube = true,
            clasificacionRemota = true
        )

        assertNull(errorTranscripcionParaChat(result))
        assertTrue(mostrarTranscripcionEnChat(result))
    }

    private fun resultadoVoz(
        texto: String = "Quiero vender papa",
        confianza: Float = 0.9f,
        intencionRemota: IntentClassifier.Intencion? = null,
        transcripcionNube: Boolean = false,
        clasificacionRemota: Boolean = false,
        requiereNube: Boolean = false
    ) = AsistenteVozFacade.Resultado(
        intencion = IntentClassifier.Intencion.DESCONOCIDA,
        entidades = extractor.extraer(texto),
        textoOriginal = texto,
        confianzaVosk = confianza,
        transcripcionNube = transcripcionNube,
        intencionRemota = intencionRemota,
        clasificacionRemota = clasificacionRemota,
        requiereTranscripcionNube = requiereNube
    )
}
