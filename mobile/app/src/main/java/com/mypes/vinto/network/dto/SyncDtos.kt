package com.mypes.vinto.network.dto

import com.google.gson.annotations.SerializedName

data class SyncBatchRequest(
    @SerializedName("transacciones") val transacciones: List<TransaccionEntrante>
)

data class TransaccionEntrante(
    @SerializedName("operationId") val operationId: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("payload") val payload: TransaccionPayload,
    @SerializedName("schemaVersion") val schemaVersion: Int
)

data class TransaccionPayload(
    @SerializedName("ventaId") val ventaId: String,
    @SerializedName("productoId") val productoId: String,
    @SerializedName("cantidad") val cantidad: Double,
    @SerializedName("precioUnitario") val precioUnitario: Double,
    @SerializedName("total") val total: Double,
    @SerializedName("formaPago") val formaPago: String,
    @SerializedName("clienteFiado") val clienteFiado: String?,
    @SerializedName("creadoEn") val creadoEn: Long
)

data class SyncBatchResponse(
    @SerializedName("procesadas") val procesadas: Int,
    @SerializedName("resultados") val resultados: List<ResultadoOperacion>
)

data class ResultadoOperacion(
    @SerializedName("operationId") val operationId: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("mensaje") val mensaje: String?
)
