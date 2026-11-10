package com.mypes.vinto.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("carnet") val carnet: String,
    @SerializedName("celular") val celular: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nombreNegocio") val nombreNegocio: String,
    @SerializedName("rubro") val rubro: String
)

data class RegisterResponse(
    @SerializedName("accessToken") val accessToken: String?,
    @SerializedName("tokenType") val tokenType: String?,
    @SerializedName("expiresIn") val expiresIn: Long
)

interface RegistroApi {
    @POST("api/v1/auth/register")
    suspend fun registrar(@Body request: RegisterRequest): RegisterResponse
}
