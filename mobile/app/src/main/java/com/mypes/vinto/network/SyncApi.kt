package com.mypes.vinto.network

import com.mypes.vinto.network.dto.SyncBatchRequest
import com.mypes.vinto.network.dto.SyncBatchResponse
import com.mypes.vinto.network.dto.ProductSyncResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SyncApi {
    @POST("api/v1/transacciones/batch")
    suspend fun enviarLote(
        @Header("Authorization") authorization: String,
        @Body body: SyncBatchRequest
    ): SyncBatchResponse

    @GET("api/v1/products/sync")
    suspend fun sincronizarProductos(
        @Header("Authorization") authorization: String,
        @Query("desde") desde: Long,
        @Query("cursorActualizadoEn") cursorActualizadoEn: Long?,
        @Query("cursorId") cursorId: String?
    ): ProductSyncResponse
}
