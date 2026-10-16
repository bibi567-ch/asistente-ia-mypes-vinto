package com.mypes.vinto.network

import com.mypes.vinto.network.dto.SyncBatchRequest
import com.mypes.vinto.network.dto.SyncBatchResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface SyncApi {
    @POST("api/v1/transacciones/batch")
    suspend fun enviarLote(
        @Header("Authorization") authorization: String,
        @Body body: SyncBatchRequest
    ): SyncBatchResponse
}
