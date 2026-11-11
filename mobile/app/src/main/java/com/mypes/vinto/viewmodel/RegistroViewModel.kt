package com.mypes.vinto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.PinHasher
import com.mypes.vinto.data.Usuario
import com.mypes.vinto.network.ApiClient
import com.mypes.vinto.network.RegisterRequest
import com.mypes.vinto.sync.AuthSession
import com.mypes.vinto.sync.SyncScheduler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class EstadoRegistro {
    data object Inicial : EstadoRegistro()
    data object Cargando : EstadoRegistro()
    data object Exito : EstadoRegistro()
    data object ExitoOffline : EstadoRegistro()
    data class Error(
        val mensaje: String,
        val puedeContinuarOffline: Boolean
    ) : EstadoRegistro()
}

class RegistroViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MypesDatabase.obtener(application)
    private val _estado = MutableStateFlow<EstadoRegistro>(EstadoRegistro.Inicial)
    val estado: StateFlow<EstadoRegistro> = _estado
    private var perfilGuardadoLocalmente = false

    fun registrar(
        nombres: String,
        apellidos: String,
        carnet: String,
        expedido: String,
        fechaNacimiento: String,
        lugarNacimiento: String,
        celular: String,
        email: String,
        password: String,
        pin: String
    ) {
        if (_estado.value is EstadoRegistro.Cargando) return

        viewModelScope.launch {
            _estado.value = EstadoRegistro.Cargando
            val nombreCompleto = "$nombres $apellidos".trim()
            try {
                val hash = PinHasher.crear(pin)
                database.usuarioDao().actualizar(
                    Usuario(
                        nombre = nombreCompleto,
                        nombres = nombres.trim(),
                        apellidos = apellidos.trim(),
                        carnet = carnet.trim(),
                        expedido = expedido.trim(),
                        fechaNacimiento = fechaNacimiento.trim(),
                        lugarNacimiento = lugarNacimiento.trim(),
                        celular = celular.trim(),
                        direccion = null,
                        email = email.trim().lowercase(),
                        nombreNegocio = "Negocio de ${nombres.trim()}",
                        rubro = "",
                        pinHash = hash.hash,
                        salt = hash.salt,
                        iteraciones = hash.iteraciones
                    )
                )
                perfilGuardadoLocalmente = true
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _estado.value = EstadoRegistro.Error(
                    error.message ?: "No se pudo guardar el perfil en el dispositivo.",
                    puedeContinuarOffline = false
                )
                return@launch
            }

            try {
                val response = ApiClient.registroApi.registrar(
                    RegisterRequest(
                        nombre = nombreCompleto,
                        nombres = nombres.trim(),
                        apellidos = apellidos.trim(),
                        carnet = carnet.trim(),
                        expedido = expedido.trim(),
                        fechaNacimiento = fechaNacimiento.trim(),
                        lugarNacimiento = lugarNacimiento.trim(),
                        celular = celular.trim(),
                        direccion = "",
                        email = email.trim().lowercase(),
                        password = password,
                    )
                )
                val accessToken = response.accessToken
                if (
                    accessToken.isNullOrBlank()
                    || !response.tokenType.equals("Bearer", ignoreCase = true)
                    || response.expiresIn <= 0
                ) {
                    _estado.value = EstadoRegistro.Error(
                        "El servidor respondió sin una sesión válida. Puedes vincular la cuenta después.",
                        puedeContinuarOffline = true
                    )
                    return@launch
                }

                AuthSession.saveToken(getApplication(), accessToken)
                SyncScheduler.programarSync(getApplication())
                _estado.value = EstadoRegistro.Exito
            } catch (error: CancellationException) {
                throw error
            } catch (error: HttpException) {
                val mensaje = when (error.code()) {
                    400 -> "El servidor rechazó los datos. Revisa el correo y que la contraseña tenga al menos 12 caracteres."
                    409 -> "Ya existe una cuenta con ese correo. Puedes vincularla desde Sincronización."
                    else -> "El perfil está guardado en este dispositivo, pero el registro en nube falló (HTTP ${error.code()})."
                }
                _estado.value = EstadoRegistro.Error(mensaje, puedeContinuarOffline = true)
            } catch (_: IOException) {
                _estado.value = EstadoRegistro.Error(
                    "El perfil está guardado en este dispositivo, pero no se pudo conectar. Reintenta o continúa sin nube.",
                    puedeContinuarOffline = true
                )
            } catch (error: Exception) {
                _estado.value = EstadoRegistro.Error(
                    error.message ?: "El perfil está guardado localmente, pero falló el registro en nube.",
                    puedeContinuarOffline = true
                )
            }
        }
    }

    fun continuarSinNube() {
        if (perfilGuardadoLocalmente && _estado.value is EstadoRegistro.Error) {
            _estado.value = EstadoRegistro.ExitoOffline
        }
    }

    fun reiniciarError() {
        if (_estado.value is EstadoRegistro.Error) _estado.value = EstadoRegistro.Inicial
    }
}
