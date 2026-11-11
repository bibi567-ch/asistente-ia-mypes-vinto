package com.mypes.vinto.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Header

data class RegisterRequest(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("carnet") val carnet: String,
    @SerializedName("expedido") val expedido: String,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String,
    @SerializedName("lugarNacimiento") val lugarNacimiento: String,
    @SerializedName("celular") val celular: String,
    @SerializedName("direccion") val direccion: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("nombreNegocio") val nombreNegocio: String? = null,
    @SerializedName("rubro") val rubro: String? = null
)

data class BusinessProfileRequest(
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

    @PUT("api/v1/auth/business")
    suspend fun actualizarNegocio(
        @Header("Authorization") authorization: String,
        @Body request: BusinessProfileRequest
    )
}
