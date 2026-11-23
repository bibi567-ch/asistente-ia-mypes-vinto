package com.mypes.vinto.nlu

object ConfirmationPolicy {
    fun requiresConfirmation(intencion: IntentClassifier.Intencion): Boolean =
        when (intencion) {
            IntentClassifier.Intencion.LLAMAR_CONTACTO,
            IntentClassifier.Intencion.ENVIAR_MENSAJE,
            IntentClassifier.Intencion.LEER_TEXTO,
            IntentClassifier.Intencion.CREAR_ALARMA,
            IntentClassifier.Intencion.ABRIR_APP,
            IntentClassifier.Intencion.LINTERNA,
            IntentClassifier.Intencion.AJUSTES_RED -> true
            else -> false
        }
}
