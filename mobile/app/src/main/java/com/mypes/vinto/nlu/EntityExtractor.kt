package com.mypes.vinto.nlu

class EntityExtractor {
    data class Entidades(
        val contacto: String? = null,
        val numeroTelefono: String? = null,
        val mensaje: String? = null,
        val hora: Int? = null,
        val minutos: Int? = null,
        val nombreApp: String? = null,
        val textoALeer: String? = null,
        val linternaEncendida: Boolean = true
    )

    private val palabrasANumeros = mapOf(
        "un" to "1", "una" to "1", "uno" to "1",
        "dos" to "2", "tres" to "3", "cuatro" to "4", "cinco" to "5",
        "seis" to "6", "siete" to "7", "ocho" to "8", "nueve" to "9",
        "diez" to "10", "once" to "11", "doce" to "12", "trece" to "13",
        "catorce" to "14", "quince" to "15", "dieciseis" to "16",
        "dieciséis" to "16", "diecisiete" to "17", "dieciocho" to "18",
        "diecinueve" to "19", "veinte" to "20", "veintiuno" to "21",
        "veintidos" to "22", "veintidós" to "22", "veintitres" to "23",
        "veinticuatro" to "24", "veinticinco" to "25", "treinta" to "30",
        "cuarenta" to "40", "cincuenta" to "50", "sesenta" to "60",
        "setenta" to "70", "ochenta" to "80", "noventa" to "90",
        "cien" to "100", "ciento" to "100", "mil" to "1000"
    )

    fun extraer(texto: String): Entidades {
        val normalizado = normalizarNumerosHablados(texto.lowercase())
        return Entidades(
            contacto = extraerContacto(normalizado),
            numeroTelefono = Regex("""\d{7,15}""").find(normalizado)?.value,
            mensaje = extraerMensaje(normalizado),
            hora = Regex("""(\d{1,2})(?:\s*:|\s+y\s+)?""").find(normalizado)?.groupValues?.get(1)?.toIntOrNull(),
            minutos = Regex("""(?:y\s+|:)(\d{1,2})""").find(normalizado)?.groupValues?.get(1)?.toIntOrNull(),
            nombreApp = Regex("""(?:abrir|abre|kichay)\s+(?:la\s+)?(?:app\s+)?([a-záéíóúñ]+)""")
                .find(normalizado)?.groupValues?.get(1),
            textoALeer = Regex("""(?:lee|leer)\s+(?:en voz alta\s+)?(.+)""")
                .find(normalizado)?.groupValues?.get(1),
            linternaEncendida = !normalizado.contains("apaga") && !normalizado.contains("apagar")
        )
    }

    private fun normalizarNumerosHablados(texto: String): String =
        texto.split(Regex("\\s+")).joinToString(" ") { palabrasANumeros[it] ?: it }

    private fun extraerNumeros(texto: String): List<Double> =
        Regex("""\d+(?:[.,]\d+)?""").findAll(texto).mapNotNull {
            it.value.replace(',', '.').toDoubleOrNull()
        }.toList()

    private fun extraerContacto(texto: String): String? =
        Regex("""(?:llama(?:r)?\s+a|mensaje\s+a|envía\s+un\s+mensaje\s+a)\s+([a-záéíóúñ]+)""")
            .find(texto)?.groupValues?.get(1)

    private fun extraerMensaje(texto: String): String? =
        Regex("""(?:diciendo|que diga|mensaje(?:\s+a\s+\w+)?)\s+(.+)$""", RegexOption.IGNORE_CASE)
            .find(texto)?.groupValues?.get(1)
}
