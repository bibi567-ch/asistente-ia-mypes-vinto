package com.mypes.vinto.asistente

/**
 * Órdenes que IA Vinto entiende para usar las herramientas del celular (alcance del perfil).
 * Mismas reglas y mismo orden que el frontend web (frontend/src/lib/nlu/intentClassifier.ts).
 */
enum class Comando(val titulo: String) {
    LLAMAR_CONTACTO("Llamar a un contacto"),
    ENVIAR_MENSAJE("Preparar un mensaje"),
    LEER_TEXTO("Leer un texto en voz alta"),
    CREAR_ALARMA("Crear una alarma"),
    ABRIR_APP("Abrir una aplicación"),
    CONSULTAR_HORA("Consultar la hora"),
    LINTERNA("Linterna"),
    AJUSTES_RED("Ajustes de red"),
    GUIA_TELEFONO("Guía paso a paso"),
    AYUDA("Ayuda"),
    DESCONOCIDO("No entendí")
}

object ClasificadorComandos {
    private val REGLAS: List<Pair<Comando, List<String>>> = listOf(
        Comando.LLAMAR_CONTACTO to listOf("llama", "llamar", "telefonear", "wawq"),
        Comando.ENVIAR_MENSAJE to listOf("envía mensaje", "enviar mensaje", "manda mensaje", "mensaje a"),
        Comando.LEER_TEXTO to listOf("lee", "leer", "qillqata"),
        Comando.CREAR_ALARMA to listOf("alarma", "despertador", "recordatorio"),
        Comando.ABRIR_APP to listOf("abre", "abrir", "apertura", "kichay"),
        Comando.CONSULTAR_HORA to listOf("qué hora", "que hora", "hora es"),
        Comando.LINTERNA to listOf("linterna", "flash", "q'anchay"),
        Comando.AJUSTES_RED to listOf("wifi", "bluetooth", "datos", "ajustes de red", "internet"),
        Comando.GUIA_TELEFONO to listOf("guía", "guia", "cómo uso", "como uso", "ayudame a usar"),
        Comando.AYUDA to listOf("ayuda", "qué puedo", "que puedo", "comandos")
    )

    /** Acciones que cambian algo en el teléfono: siempre se confirman antes de ejecutar. */
    private val CON_CONFIRMACION = setOf(
        Comando.LLAMAR_CONTACTO, Comando.ENVIAR_MENSAJE, Comando.LEER_TEXTO,
        Comando.CREAR_ALARMA, Comando.ABRIR_APP, Comando.LINTERNA, Comando.AJUSTES_RED
    )

    fun clasificar(texto: String): Comando {
        val normalizado = texto.lowercase().trim()
        for ((comando, expresiones) in REGLAS) {
            if (expresiones.any { normalizado.contains(it) }) return comando
        }
        return Comando.DESCONOCIDO
    }

    fun requiereConfirmacion(comando: Comando): Boolean = comando in CON_CONFIRMACION
}
