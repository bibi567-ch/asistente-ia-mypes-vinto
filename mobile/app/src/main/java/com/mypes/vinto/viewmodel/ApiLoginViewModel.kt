package com.mypes.vinto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.network.ApiClient
import com.mypes.vinto.network.LoginRequest
import com.mypes.vinto.network.RegisterRequest
import com.mypes.vinto.sync.AuthSession
import com.mypes.vinto.sync.SyncScheduler
import kotlinx.coroutines.CancellationException
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

    fun registrarCuentaLocal(email: String, password: String) {
        if (_estado.value is ApiLoginEstado.Iniciando) return
        if (email.isBlank() || password.length !in 12..256) {
            _estado.value = ApiLoginEstado.Error("Ingresa el correo del perfil y una contraseña de 12 a 256 caracteres.")
            return
        }

        viewModelScope.launch {
            _estado.value = ApiLoginEstado.Iniciando
            try {
                val usuario = MypesDatabase.obtener(getApplication()).usuarioDao().obtener()
                if (usuario == null) {
                    _estado.value = ApiLoginEstado.Error("No hay un perfil local para registrar en la nube.")
                    return@launch
                }
                if (!usuario.email.equals(email.trim(), ignoreCase = true)) {
                    _estado.value = ApiLoginEstado.Error("Usa el mismo correo guardado en el perfil local.")
                    return@launch
                }
                val nombreNegocio = usuario.nombreNegocio?.takeIf(String::isNotBlank)
                val rubro = usuario.rubro?.takeIf(String::isNotBlank)
                if (
                    usuario.nombres.isBlank()
                    || usuario.apellidos.isBlank()
                    || usuario.carnet.isNullOrBlank()
                    || usuario.expedido.isNullOrBlank()
                    || usuario.fechaNacimiento.isNullOrBlank()
                    || usuario.lugarNacimiento.isNullOrBlank()
                    || usuario.celular.isNullOrBlank()
                    || nombreNegocio == null
                    || rubro == null
                ) {
                    _estado.value = ApiLoginEstado.Error("Completa los datos personales y del negocio antes de crear la cuenta.")
                    return@launch
                }

                val response = ApiClient.registroApi.registrar(
                    RegisterRequest(
                        nombre = usuario.nombre,
                        nombres = usuario.nombres,
                        apellidos = usuario.apellidos,
                        carnet = usuario.carnet,
                        expedido = usuario.expedido,
                        fechaNacimiento = usuario.fechaNacimiento,
                        lugarNacimiento = usuario.lugarNacimiento,
                        celular = usuario.celular,
                        direccion = usuario.direccion.orEmpty(),
                        email = email.trim().lowercase(),
                        password = password,
                        nombreNegocio = nombreNegocio,
                        rubro = rubro
                    )
                )
                val accessToken = response.accessToken
                if (
                    accessToken.isNullOrBlank()
                    || !response.tokenType.equals("Bearer", ignoreCase = true)
                    || response.expiresIn <= 0
                ) {
                    _estado.value = ApiLoginEstado.Error("El servidor respondió sin una sesión válida.")
                    return@launch
                }

                AuthSession.saveToken(getApplication(), accessToken)
                SyncScheduler.programarSync(getApplication())
                _estado.value = ApiLoginEstado.Exito
            } catch (error: CancellationException) {
                throw error
            } catch (error: HttpException) {
                _estado.value = ApiLoginEstado.Error(
                    when (error.code()) {
                        400 -> "El servidor rechazó los datos del perfil o la contraseña."
                        409 -> "Ya existe una cuenta con ese correo. Inicia sesión para vincularla."
                        else -> "No se pudo crear la cuenta en la nube (HTTP ${error.code()})."
                    }
                )
            } catch (_: IOException) {
                _estado.value = ApiLoginEstado.Error("No se pudo conectar con el servidor. Revisa tu conexión.")
            } catch (error: Exception) {
                _estado.value = ApiLoginEstado.Error(
                    error.message ?: "No se pudo crear la cuenta en la nube."
                )
            }
        }
    }

    fun reiniciarError() {
        if (_estado.value is ApiLoginEstado.Error) _estado.value = ApiLoginEstado.Inicial
    }
}
