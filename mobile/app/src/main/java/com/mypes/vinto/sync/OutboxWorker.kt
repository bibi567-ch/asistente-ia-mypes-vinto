package com.mypes.vinto.sync

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mypes.vinto.data.OutboxEntry
import com.mypes.vinto.data.VintoDatabase
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class VoiceLogRequest(
    val eventId: String,
    val texto: String,
    val intencion: String,
    val confirmado: Boolean,
    val ejecutado: Boolean,
    val timestamp: Long
) {
    companion object {
        fun from(entry: OutboxEntry) = VoiceLogRequest(
            eventId = entry.id,
            texto = entry.textoComando,
            intencion = entry.intencion,
            confirmado = entry.confirmado,
            ejecutado = entry.ejecutado,
            timestamp = entry.timestamp
        )
    }
}

private data class VoiceLogResponse(val accepted: Boolean)

private interface VoiceLogApi {
    @POST("api/v1/logs/comandos")
    suspend fun enviar(
        @Header("Authorization") authorization: String,
        @Body log: VoiceLogRequest
    ): VoiceLogResponse
}

class OutboxWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val preferences = applicationContext.getSharedPreferences("voice_cloud", Context.MODE_PRIVATE)
        val baseUrl = preferences.getString("api_base_url", null)?.trim()
        val token = preferences.getString("access_token", null)?.trim()
        if (baseUrl.isNullOrEmpty() || token.isNullOrEmpty()) {
            return Result.failure(workDataOf("error" to "ASR_API_URL_OR_ACCESS_TOKEN_MISSING"))
        }

        val api = Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VoiceLogApi::class.java)
        val dao = VintoDatabase.obtener(applicationContext).outboxDao()

        return try {
            dao.pendientes().forEach { entry ->
                api.enviar("Bearer $token", VoiceLogRequest.from(entry))
                dao.marcarSincronizado(entry.id)
            }
            Result.success()
        } catch (_: IOException) {
            Result.retry()
        } catch (error: HttpException) {
            if (error.code() >= 500) Result.retry()
            else Result.failure(workDataOf("error" to "VOICE_LOG_HTTP_${error.code()}"))
        }
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "voice-command-log-sync"

        fun programar(context: Context) {
            val request = OneTimeWorkRequestBuilder<OutboxWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                request
            )
        }
    }
}
