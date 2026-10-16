package com.mypes.vinto.nlu

class EntityExtractor {
    data class Entidades(
        val producto: String? = null,
        val cantidad: Double? = null,
        val unidad: String? = null,
        val monto: Double? = null,
        val cliente: String? = null,
        val formaPago: String? = null
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
        "cien" to "100", "ciento" to "100", "mil" to "1000"
    )

    fun extraer(texto: String): Entidades {
        val normalizado = normalizarNumerosHablados(texto.lowercase())
        return Entidades(
            producto = extraerProducto(normalizado),
            cantidad = extraerNumeros(normalizado).firstOrNull(),
            unidad = unidades.firstOrNull(normalizado::contains),
            monto = extraerMonto(normalizado),
            cliente = extraerCliente(normalizado),
            formaPago = extraerFormaPago(normalizado)
        )
    }

    private fun normalizarNumerosHablados(texto: String): String =
        texto.split(Regex("\\s+")).joinToString(" ") { palabrasANumeros[it] ?: it }

    private fun extraerNumeros(texto: String): List<Double> =
        Regex("""\d+(?:[.,]\d+)?""").findAll(texto).mapNotNull {
            it.value.replace(',', '.').toDoubleOrNull()
        }.toList()

    private fun extraerProducto(texto: String): String? {
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
        Regex("""(\d+(?:[.,]\d+)?)\s*(?:bolivianos?|bs\.?|pesos?)""")
            .find(texto)?.groupValues?.get(1)?.replace(',', '.')?.toDoubleOrNull()

    private fun extraerCliente(texto: String): String? =
        Regex("""(?:a|para)\s+(?:doña|don|casera|casero)\s+(\w+)""")
            .find(texto)?.groupValues?.get(1)

    private fun extraerFormaPago(texto: String): String? = when {
        "fiado" in texto -> "FIADO"
        "al contado" in texto || "al tiro" in texto -> "CONTADO"
        else -> null
    }
}
