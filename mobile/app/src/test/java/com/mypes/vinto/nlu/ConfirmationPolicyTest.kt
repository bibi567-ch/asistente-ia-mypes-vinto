package com.mypes.vinto.nlu

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfirmationPolicyTest {
    @Test
    fun `phone actions require confirmation while informational queries do not`() {
        assertTrue(ConfirmationPolicy.requiresConfirmation(IntentClassifier.Intencion.LLAMAR_CONTACTO))
        assertTrue(ConfirmationPolicy.requiresConfirmation(IntentClassifier.Intencion.ENVIAR_MENSAJE))
        assertTrue(ConfirmationPolicy.requiresConfirmation(IntentClassifier.Intencion.ABRIR_APP))
        assertTrue(ConfirmationPolicy.requiresConfirmation(IntentClassifier.Intencion.CREAR_ALARMA))
        assertTrue(ConfirmationPolicy.requiresConfirmation(IntentClassifier.Intencion.LINTERNA))
        assertFalse(ConfirmationPolicy.requiresConfirmation(IntentClassifier.Intencion.CONSULTAR_HORA))
    }
}
