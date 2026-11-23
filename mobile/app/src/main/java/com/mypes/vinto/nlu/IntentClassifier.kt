package com.mypes.vinto.nlu

class IntentClassifier {
    enum class Intencion {
        LLAMAR_CONTACTO, ENVIAR_MENSAJE, LEER_TEXTO, CREAR_ALARMA,
        ABRIR_APP, CONSULTAR_HORA, LINTERNA, AJUSTES_RED,
        GUIA_TELEFONO, AYUDA, DESCONOCIDO
    }

    fun clasificar(texto: String): Intencion {
        val normalizado = texto.lowercase().trim()
        return when {
            contiene(normalizado, "llama", "llamar", "telefonear", "wawq") -> Intencion.LLAMAR_CONTACTO
            contiene(normalizado, "envía mensaje", "enviar mensaje", "manda mensaje", "mensaje a") -> Intencion.ENVIAR_MENSAJE
            contiene(normalizado, "lee", "leer", "qillqata") -> Intencion.LEER_TEXTO
            contiene(normalizado, "alarma", "despertador", "recordatorio") -> Intencion.CREAR_ALARMA
            contiene(normalizado, "abre", "abrir", "apertura", "kichay") -> Intencion.ABRIR_APP
            contiene(normalizado, "qué hora", "que hora", "hora es") -> Intencion.CONSULTAR_HORA
            contiene(normalizado, "linterna", "flash", "q'anchay") -> Intencion.LINTERNA
            contiene(normalizado, "wifi", "bluetooth", "datos", "ajustes de red", "internet") -> Intencion.AJUSTES_RED
            contiene(normalizado, "guía", "guia", "cómo uso", "como uso", "ayudame a usar") -> Intencion.GUIA_TELEFONO
            contiene(normalizado, "ayuda", "qué puedo", "que puedo", "comandos") -> Intencion.AYUDA
            else -> Intencion.DESCONOCIDO
        }
    }

    private fun contiene(texto: String, vararg expresiones: String): Boolean =
        expresiones.any(texto::contains)
}
