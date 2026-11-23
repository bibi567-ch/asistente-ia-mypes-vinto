package com.mypes.vinto.data

/** Resultado de consulta (no es tabla): productos más vendidos. */
data class RankingProducto(
    val nombre: String,
    val cantidad: Double,
    val total: Double
)
