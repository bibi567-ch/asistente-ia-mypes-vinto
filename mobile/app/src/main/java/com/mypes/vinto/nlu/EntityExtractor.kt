package com.mypes.vinto.nlu

import java.text.Normalizer
import java.util.Locale

class EntityExtractor {
    data class Entidades(
        val producto: String? = null,
        val cantidad: Double? = null,
        val unidad: String? = null,
        val monto: Double? = null,
        val cliente: String? = null,
        val formaPago: String? = null,
        val numeroTelefono: String? = null,
        val hora: Int? = null,
        val minutos: Int? = null,
        val nombreApp: String? = null,
        val textoALeer: String? = null,
        val mensaje: String? = null,
        val linternaEncendida: Boolean = true
    )

    private val unidades = listOf(
        "cuartilla", "arroba", "amarrito", "amarre", "caja", "unidad",
        "docena", "kilo", "litro", "metro", "medio", "cuarto", "entero",
        "plato", "vaso", "bolsa"
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
        "cien" to "100", "ciento" to "100", "mil" to "1000",
        "juk" to "1", "iskay" to "2", "kimsa" to "3", "tawa" to "4",
        "phichqa" to "5", "suqta" to "6", "qanchis" to "7", "pusaq" to "8",
        "jisqun" to "9", "chunka" to "10", "pachak" to "100"
    )
    private val frasesNumericasQuechua = listOf(
        "chunka jukniyuq" to "11",
        "chunka iskayniyuq" to "12",
        "iskay chunka" to "20"
    )

    private val digitosHablados = mapOf(
        "cero" to '0', "uno" to '1', "un" to '1', "una" to '1',
        "dos" to '2', "tres" to '3', "cuatro" to '4', "cinco" to '5',
        "seis" to '6', "siete" to '7', "ocho" to '8', "nueve" to '9'
    )
    private val conectoresPin = setOf("mi", "el", "es", "pin", "clave", "por", "favor", "y")

    fun extraerPin(texto: String): String? {
        val tokens = Regex("""[\p{L}\d]+""")
            .findAll(texto.lowercase(Locale.ROOT))
            .map { Normalizer.normalize(it.value, Normalizer.Form.NFD).replace(Regex("\\p{M}+"), "") }
            .toList()
        val digitos = tokens.mapNotNull { token ->
            when {
                token in conectoresPin -> null
                token.length == 1 && token[0].isDigit() -> token[0]
                else -> digitosHablados[token]
            }
        }
        if (tokens.any { it !in conectoresPin && it.length != 1 && it !in digitosHablados }) return null
        return digitos.joinToString("").takeIf { it.length == 4 }
    }

    fun extraer(texto: String): Entidades {
        val normalizado = normalizarNumerosHablados(texto.lowercase(Locale.ROOT))
        return Entidades(
            producto = extraerProducto(normalizado),
            cantidad = extraerNumeros(normalizado).firstOrNull(),
            unidad = unidades.firstOrNull(normalizado::contains),
            monto = extraerMonto(normalizado),
            cliente = extraerCliente(texto),
            formaPago = extraerFormaPago(normalizado),
            numeroTelefono = Regex("""(?:\+?\d[\d\s-]{6,}\d)""")
                .find(texto)?.value?.filter { it.isDigit() },
            hora = Regex("""\b(?:a las|las)\s+(\d{1,2})\b""", RegexOption.IGNORE_CASE)
                .find(texto)?.groupValues?.get(1)?.toIntOrNull(),
            minutos = Regex("""\b(?:a las|las)\s+\d{1,2}:(\d{2})\b""", RegexOption.IGNORE_CASE)
                .find(texto)?.groupValues?.get(1)?.toIntOrNull(),
            nombreApp = Regex("""\b(?:abre|abrir)\s+([\p{L}\d]+)""", RegexOption.IGNORE_CASE)
                .find(texto)?.groupValues?.get(1),
            textoALeer = Regex("""\b(?:lee|leer)\s+(?:en voz alta\s+)?(.+)""", RegexOption.IGNORE_CASE)
                .find(texto)?.groupValues?.get(1)?.trim(),
            mensaje = Regex("""\b(?:diciendo|mensaje)\s+(.+)""", RegexOption.IGNORE_CASE)
                .find(texto)?.groupValues?.get(1)?.trim(),
            linternaEncendida = !normalizado.contains("apaga")
        )
    }

    private fun normalizarNumerosHablados(texto: String): String =
        frasesNumericasQuechua.fold(texto) { resultado, (frase, numero) ->
            Regex("""(?<![\p{L}\d])${Regex.escape(frase)}(?![\p{L}\d])""")
                .replace(resultado, numero)
        }
            .split(Regex("\\s+"))
            .joinToString(" ") { palabrasANumeros[it] ?: it }

    private fun extraerNumeros(texto: String): List<Double> =
        Regex("""\d+(?:[.,]\d+)?""").findAll(texto).mapNotNull {
            it.value.replace(',', '.').toDoubleOrNull()
        }.toList()

    private fun extraerProducto(texto: String): String? {
        val productoDespuesDeCantidad = Regex("""\b\d+(?:[.,]\d+)?\s+([\p{L}]+)\b""")
            .findAll(texto)
            .map { it.groupValues[1] }
            .firstOrNull { palabra ->
                palabra !in unidades &&
                    palabra.removeSuffix("s") !in unidades &&
                    palabra !in setOf("boliviano", "bolivianos", "peso", "pesos", "bs")
            }
        if (productoDespuesDeCantidad != null) return productoDespuesDeCantidad

        val palabras = texto.split(" ")
        palabras.forEachIndexed { indice, palabra ->
            if (palabra == "de" && indice + 1 < palabras.size) return palabras[indice + 1]
            if (palabra.startsWith("vend") && indice + 1 < palabras.size) {
                val siguiente = palabras[indice + 1]
                if (!siguiente.all(Char::isDigit)) return siguiente
            }
        }
        return null
    }

    private fun extraerMonto(texto: String): Double? =
        Regex("""(\d+(?:[.,]\d+)?)\s*(?:bolivianos?|bs\.?|pesos?|qullqi)""")
            .find(texto)?.groupValues?.get(1)?.replace(',', '.')?.toDoubleOrNull()

    private fun extraerCliente(texto: String): String? =
        Regex("""(?:a|para)\s+(?:doña|don|casera|casero)\s+([\p{L}]+)""", RegexOption.IGNORE_CASE)
            .find(texto)?.groupValues?.get(1)
            ?.lowercase(Locale.ROOT)
            ?.replaceFirstChar { it.titlecase(Locale.ROOT) }

    private fun extraerFormaPago(texto: String): String? = when {
        "fiado" in texto -> "FIADO"
        "al contado" in texto || "al tiro" in texto -> "CONTADO"
        else -> null
    }
}
