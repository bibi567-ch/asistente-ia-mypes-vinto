package com.mypes.vinto.nlu

class IntentClassifier {
    enum class Intencion {
        VENTA, COMPRA, FIADO, COBRO,
        CONSULTA_STOCK, CONSULTA_VENTAS, CANCELAR, DESCONOCIDO
    }

    fun clasificar(texto: String): Intencion {
        val normalizado = texto.lowercase().trim()
        return when {
            normalizado.contains("cancelar") ||
                normalizado.contains("me equivoqu") ||
                normalizado.contains("anular") -> Intencion.CANCELAR

            normalizado.contains("vendi hoy") ||
                normalizado.contains("vendí hoy") ||
                normalizado.contains("resumen") ||
                normalizado.contains("total del dia") ||
                normalizado.contains("total del día") -> Intencion.CONSULTA_VENTAS

            normalizado.contains("vend") ||
                normalizado.contains("venta") -> Intencion.VENTA

            normalizado.contains("compr") ||
                normalizado.contains("compra") -> Intencion.COMPRA

            normalizado.contains("fiad") -> Intencion.FIADO

            normalizado.contains("cobr") ||
                normalizado.contains("cobro") ||
                normalizado.contains("pago") -> Intencion.COBRO

            normalizado.contains("cuanto") ||
                normalizado.contains("cuánto") ||
                normalizado.contains("stock") ||
                normalizado.contains("queda") ||
                normalizado.contains("quedan") -> Intencion.CONSULTA_STOCK

            else -> Intencion.DESCONOCIDO
        }
    }
}
