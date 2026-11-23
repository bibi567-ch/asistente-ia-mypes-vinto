package com.mypes.vinto.asistente

import java.text.Normalizer

/** Datos que se sacan de la orden hablada. */
data class DatosComando(
    val numeroTelefono: String? = null,
    val contacto: String? = null,
    val mensaje: String? = null,
    val hora: Int? = null,
    val minutos: Int? = null,
    val nombreApp: String? = null,
    val linternaEncendida: Boolean = true,
    val textoALeer: String? = null
)

/** Mismo comportamiento que frontend/src/lib/nlu/entityExtractor.ts. */
object ExtractorComandos {
    private val NUMEROS = mapOf(
        "una" to 1, "uno" to 1, "un" to 1, "dos" to 2, "tres" to 3, "cuatro" to 4, "cinco" to 5,
        "seis" to 6, "siete" to 7, "ocho" to 8, "nueve" to 9, "diez" to 10, "once" to 11,
        "doce" to 12, "trece" to 13, "catorce" to 14, "quince" to 15, "veinte" to 20,
        "treinta" to 30, "cuarenta" to 40, "cincuenta" to 50, "cero" to 0
    )
    private val DIGITOS = mapOf(
        "cero" to "0", "uno" to "1", "una" to "1", "dos" to "2", "tres" to "3", "cuatro" to "4",
        "cinco" to "5", "seis" to "6", "siete" to "7", "ocho" to "8", "nueve" to "9"
    )
    val APPS = listOf(
        "whatsapp", "youtube", "chrome", "facebook", "tiktok", "telegram", "maps", "mapas",
        "cámara", "camara", "galería", "galeria", "teléfono", "telefono", "mensajes",
        "ajustes", "configuración", "configuracion"
    )

    private val REGEX_APAGAR = Regex("\\b(apaga|apagar|desactiva)")
    private val REGEX_DIGITOS = Regex("\\+?\\d{7,}")
    private val REGEX_MENSAJE = Regex("(?:que diga|diciendo|dile que|:)\\s*(.+)$", RegexOption.IGNORE_CASE)
    private val REGEX_CONTACTO =
        Regex("(?:llama(?:r)?|telefonea(?:r)?|mensaje)\\s+(?:a\\s+)?((?:mi|don|doña)\\s+)?(\\p{L}+)")
    private val REGEX_HORA =
        Regex("(?:a las|a la)\\s+(\\d{1,2}|\\p{L}+)(?::(\\d{2}))?(?:\\s+y\\s+(media|cuarto|\\d{1,2}|\\p{L}+))?")
    private val REGEX_HORA_PUNTOS = Regex("\\b(\\d{1,2}):(\\d{2})\\b")
    private val REGEX_TARDE = Regex("de la (tarde|noche)")
    private val REGEX_LEER =
        Regex("(?:lee|leer)\\s+(?:esto|este texto|el texto)?\\s*:?\\s*(.+)$", RegexOption.IGNORE_CASE)
    private val NO_CONTACTO = setOf("un", "una", "el", "la", "que")

    fun sinTildes(s: String): String =
        Normalizer.normalize(s, Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "")

    private fun aNumero(t: String?): Int? {
        if (t.isNullOrEmpty()) return null
        return t.toIntOrNull() ?: NUMEROS[sinTildes(t)]
    }

    fun extraer(texto: String): DatosComando {
        val t = texto.lowercase().trim()

        // Número: dígitos (7 o más) o dígitos hablados ("siete siete dos ...").
        var numero: String? = REGEX_DIGITOS.find(t.replace(Regex("[\\s-]"), ""))?.value
        if (numero == null) {
            val hablados = t.split(Regex("\\s+")).mapNotNull { DIGITOS[sinTildes(it)] }.joinToString("")
            if (hablados.length >= 7) numero = hablados
        }

        val mensaje = REGEX_MENSAJE.find(texto)?.groupValues?.get(1)?.trim()

        var contacto: String? = null
        REGEX_CONTACTO.find(t)?.let { m ->
            val nombre = m.groupValues[2]
            if (nombre !in NO_CONTACTO) contacto = (m.groupValues[1] + nombre).trim()
        }

        var hora: Int? = null
        var minutos: Int? = null
        val h = REGEX_HORA.find(t) ?: REGEX_HORA_PUNTOS.find(t)
        if (h != null) {
            var hh = aNumero(h.groupValues[1])
            val g2 = h.groupValues.getOrNull(2).orEmpty()
            val g3 = h.groupValues.getOrNull(3).orEmpty()
            val mm = when {
                g2.isNotEmpty() -> g2.toInt()
                g3 == "media" -> 30
                g3 == "cuarto" -> 15
                else -> aNumero(g3) ?: 0
            }
            if (hh != null && REGEX_TARDE.containsMatchIn(t) && hh < 12) hh += 12
            if (hh != null && hh in 0..23 && mm in 0..59) {
                hora = hh
                minutos = mm
            }
        }

        val leer = REGEX_LEER.find(texto)?.groupValues?.get(1)?.trim()

        return DatosComando(
            numeroTelefono = numero,
            contacto = contacto,
            mensaje = mensaje,
            hora = hora,
            minutos = minutos,
            nombreApp = APPS.firstOrNull { t.contains(it) },
            linternaEncendida = !REGEX_APAGAR.containsMatchIn(t),
            textoALeer = leer?.takeIf { it.length > 3 }
        )
    }
}
