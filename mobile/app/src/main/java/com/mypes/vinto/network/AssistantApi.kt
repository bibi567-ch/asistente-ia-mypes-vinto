package com.mypes.vinto.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class AssistantQueryRequest(val pregunta: String, val idioma: String)
data class AssistantQueryResponse(val respuesta: String)

data class VoiceCorrection(
    val eventId: String,
    val textoOriginal: String,
    val textoCorregido: String,
    val intencion: String,
    val fecha: Long
)

data class LearningFeedbackRequest(val logs: List<VoiceCorrection>)
data class LearningFeedbackResponse(val accepted: Int)

interface AssistantApi {
    @POST("api/v1/assistant/query")
    suspend fun consultar(
        @Header("Authorization") authorization: String,
        @Body request: AssistantQueryRequest
    ): AssistantQueryResponse
}

interface LearningApi {
    @POST("api/v1/learning/feedback")
    suspend fun enviarCorrecciones(
        @Header("Authorization") authorization: String,
        @Body request: LearningFeedbackRequest
    ): LearningFeedbackResponse
}
