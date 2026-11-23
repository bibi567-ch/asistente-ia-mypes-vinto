package com.mypes.vinto.nlu

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header

data class CloudAsrResponse(val texto: String)

interface CloudAsrApi {
    @Multipart
    @POST("api/v1/asr/transcribe")
    suspend fun transcribe(
        @Header("Authorization") authorization: String,
        @Part audio: MultipartBody.Part
    ): CloudAsrResponse
}

class CloudAsrService(private val baseUrl: String, private val accessToken: String) {
    private val api = Retrofit.Builder()
        .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CloudAsrApi::class.java)

    suspend fun transcribe(audio: RequestBody, filename: String = "voice.wav"): String =
        api.transcribe(
            "Bearer $accessToken",
            MultipartBody.Part.createFormData("audio", filename, audio)
        ).texto

    suspend fun transcribeIfNeeded(
        context: Context,
        audio: RequestBody,
        voskConfidence: Float,
        filename: String = "voice.wav"
    ): String? {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return null
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return null
        val online = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        if (!CloudAsrFallback.shouldFallback(online, voskConfidence)) return null
        return transcribe(audio, filename)
    }
}

object CloudAsrFallback {
    fun shouldFallback(isOnline: Boolean, confidence: Float): Boolean =
        isOnline && confidence.isFinite() && confidence < 0.70f
}
