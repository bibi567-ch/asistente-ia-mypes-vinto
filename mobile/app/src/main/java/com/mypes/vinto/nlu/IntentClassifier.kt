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
        AYUDA,
        REGISTRAR_VENTA,
        CONSULTAR_STOCK,
        CONSULTAR_VENTAS,
        AGREGAR_PRODUCTO,
        SALUDO,
        DESCONOCIDA
    }

    fun clasificar(texto: String): Intencion {
        val normalizado = java.text.Normalizer.normalize(
            texto.lowercase().trim(),
            java.text.Normalizer.Form.NFD
        ).replace(Regex("\\p{Mn}+"), "")
        return when {
            contiene(normalizado, "cuanto vendi", "cuanto vendí", "ventas de hoy", "total de ventas", "resumen de ventas") ->
                Intencion.CONSULTAR_VENTAS
            contiene(normalizado, "stock", "inventario", "existencias", "cuanto queda", "qué queda", "que queda", "yupay") ->
                Intencion.CONSULTAR_STOCK
            contiene(normalizado, "agrega producto", "agregar producto", "añade producto", "anade producto", "nuevo producto") ->
                Intencion.AGREGAR_PRODUCTO
            contiene(normalizado, "registrar venta", "registra venta", "anota venta", "hacer una venta", "quiero vender", "vender producto", "qhatuy") ->
                Intencion.REGISTRAR_VENTA
            contiene(normalizado, "imaynalla", "allillan") -> Intencion.SALUDO
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
            else -> Intencion.DESCONOCIDA
        }
    }

    fun clasificarResultadoRemoto(intent: String?, confidence: Float?): Intencion? {
        if (confidence == null || !confidence.isFinite() || confidence < MIN_REMOTE_CONFIDENCE) return null
        return when (intent) {
            "registrar_venta" -> Intencion.REGISTRAR_VENTA
            "consultar_stock" -> Intencion.CONSULTAR_STOCK
            "consultar_ventas" -> Intencion.CONSULTAR_VENTAS
            "agregar_producto" -> Intencion.AGREGAR_PRODUCTO
            "consulta_cognitiva" -> Intencion.CONSULTA_COGNITIVA
            "ayuda" -> Intencion.AYUDA
            else -> null
        }
    }

    private fun contiene(texto: String, vararg expresiones: String): Boolean =
        expresiones.any(texto::contains)

    private companion object {
        const val MIN_REMOTE_CONFIDENCE = 0.62f
    }
}
