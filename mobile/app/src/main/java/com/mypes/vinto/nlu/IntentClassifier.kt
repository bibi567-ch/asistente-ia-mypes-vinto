package com.mypes.vinto.nlu

class IntentClassifier {
    enum class Intencion {
        LLAMAR_CONTACTO,
        ENVIAR_MENSAJE,
        LEER_TEXTO,
        CREAR_ALARMA,
        ABRIR_APP,
        CONSULTAR_HORA,
        LINTERNA,
        AJUSTES_RED,
        GUIA_TELEFONO,
        CONSULTA_COGNITIVA,
        AYUDA
    }

    fun clasificar(texto: String): Intencion {
        val normalizado = java.text.Normalizer.normalize(
            texto.lowercase().trim(),
            java.text.Normalizer.Form.NFD
        ).replace(Regex("\\p{Mn}+"), "")
        return when {
            contiene(normalizado, "llama", "llamar", "telefonear", "wawq") -> Intencion.LLAMAR_CONTACTO
            contiene(normalizado, "envia mensaje", "enviar mensaje", "manda mensaje", "mensaje a") -> Intencion.ENVIAR_MENSAJE
            contiene(normalizado, "lee", "leer", "qillqata") -> Intencion.LEER_TEXTO
            contiene(normalizado, "alarma", "despertador", "recordatorio") -> Intencion.CREAR_ALARMA
            contiene(normalizado, "abre", "abrir", "apertura", "kichay") -> Intencion.ABRIR_APP
            contiene(normalizado, "que hora", "hora es") -> Intencion.CONSULTAR_HORA
            contiene(normalizado, "linterna", "flash", "q'anchay") -> Intencion.LINTERNA
            contiene(normalizado, "wifi", "bluetooth", "datos", "ajustes de red", "internet") -> Intencion.AJUSTES_RED
            contiene(normalizado, "guia", "como uso", "ayudame a usar") -> Intencion.GUIA_TELEFONO
            contiene(normalizado, "como", "que es", "iva", "ganancia", "contabilidad", "administracion") ->
                Intencion.CONSULTA_COGNITIVA
            contiene(normalizado, "ayuda", "que puedo", "comandos") -> Intencion.AYUDA
            else -> Intencion.AYUDA
        }
    }

    private fun contiene(texto: String, vararg expresiones: String): Boolean =
        expresiones.any(texto::contains)
}
