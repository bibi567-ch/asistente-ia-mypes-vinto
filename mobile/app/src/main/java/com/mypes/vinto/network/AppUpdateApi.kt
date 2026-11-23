package com.mypes.vinto.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class AppUpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val url: String,
    val sha256: String,
    val size: Long
)

interface AppUpdateApi {
    @GET("api/v1/app-updates/latest")
    suspend fun latest(): AppUpdateInfo
}

object AppUpdateClient {
    fun create(baseUrl: String): AppUpdateApi =
        Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AppUpdateApi::class.java)
}
