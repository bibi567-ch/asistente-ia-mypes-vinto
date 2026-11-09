package com.mypes.vinto.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("accessToken") val accessToken: String?,
    @SerializedName("tokenType") val tokenType: String?,
    @SerializedName("expiresIn") val expiresIn: Long
)

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun iniciarSesion(@Body body: LoginRequest): LoginResponse
}
