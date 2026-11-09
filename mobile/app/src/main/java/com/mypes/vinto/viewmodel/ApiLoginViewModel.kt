package com.mypes.vinto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.network.ApiClient
import com.mypes.vinto.network.LoginRequest
import com.mypes.vinto.sync.AuthSession
import com.mypes.vinto.sync.SyncScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class ApiLoginEstado {
    data object Inicial : ApiLoginEstado()
    data object Iniciando : ApiLoginEstado()
    data object Exito : ApiLoginEstado()
    data class Error(val mensaje: String) : ApiLoginEstado()
}

class ApiLoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _estado = MutableStateFlow<ApiLoginEstado>(ApiLoginEstado.Inicial)
    val estado: StateFlow<ApiLoginEstado> = _estado

    fun iniciarSesion(email: String, password: String) {
        if (_estado.value is ApiLoginEstado.Iniciando) return
        if (email.isBlank() || password.isBlank()) {
            _estado.value = ApiLoginEstado.Error("Ingresa el correo y la contraseña de tu cuenta.")
            return
        }

        viewModelScope.launch {
            _estado.value = ApiLoginEstado.Iniciando
            try {
                val response = ApiClient.authApi.iniciarSesion(
                    LoginRequest(email.trim(), password)
                )
                val accessToken = response.accessToken
                if (
                    accessToken.isNullOrBlank()
                    || !response.tokenType.equals("Bearer", ignoreCase = true)
                    || response.expiresIn <= 0
                ) {
                    _estado.value = ApiLoginEstado.Error("La API devolvió una sesión no válida.")
                    return@launch
                }

                AuthSession.saveToken(getApplication(), accessToken)
                SyncScheduler.programarSync(getApplication())
                _estado.value = ApiLoginEstado.Exito
            } catch (error: HttpException) {
                _estado.value = ApiLoginEstado.Error(
                    if (error.code() == 401) "Correo o contraseña incorrectos."
                    else "No se pudo iniciar sesión (HTTP ${error.code()})."
                )
            } catch (_: IOException) {
                _estado.value = ApiLoginEstado.Error(
                    "No se pudo conectar con el servidor. Revisa la dirección y tu conexión."
                )
            } catch (error: Exception) {
                _estado.value = ApiLoginEstado.Error(
                    error.message ?: "No se pudo iniciar sesión con la API."
                )
            }
        }
    }

    fun reiniciarError() {
        if (_estado.value is ApiLoginEstado.Error) _estado.value = ApiLoginEstado.Inicial
    }
}
