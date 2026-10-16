package com.mypes.vinto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.PinHasher
import com.mypes.vinto.data.PoliticaAcceso
import com.mypes.vinto.data.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginEstado {
    data object Inicial : LoginEstado()
    data object Verificando : LoginEstado()
    data object Exito : LoginEstado()
    data class Error(val mensaje: String) : LoginEstado()
    data class Bloqueado(val segundosRestantes: Long) : LoginEstado()
}

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MypesDatabase.obtener(application)
    private val _estado = MutableStateFlow<LoginEstado>(LoginEstado.Inicial)
    val estado: StateFlow<LoginEstado> = _estado

    fun verificarPin(pin: String) {
        if (pin.length != 4 || _estado.value is LoginEstado.Verificando) return
        viewModelScope.launch {
            _estado.value = LoginEstado.Verificando
            try {
                val usuario = db.usuarioDao().obtener()
                if (usuario == null) {
                    val hash = PinHasher.crear(pin)
                    db.usuarioDao().insertar(
                        Usuario(nombre = "Comerciante", pinHash = hash.hash, salt = hash.salt, iteraciones = hash.iteraciones)
                    )
                    _estado.value = LoginEstado.Exito
                    return@launch
                }

                val ahora = System.currentTimeMillis()
                val acceso = PoliticaAcceso.EstadoAcceso(usuario.intentosFallidos, usuario.bloqueadoHasta)
                if (PoliticaAcceso.estaBloqueado(acceso, ahora)) {
                    _estado.value = LoginEstado.Bloqueado(PoliticaAcceso.segundosRestantes(acceso, ahora))
                    return@launch
                }

                if (PinHasher.verificar(pin, PinHasher.PinHash(usuario.pinHash, usuario.salt, usuario.iteraciones))) {
                    db.usuarioDao().actualizar(usuario.copy(intentosFallidos = 0, bloqueadoHasta = 0))
                    _estado.value = LoginEstado.Exito
                } else {
                    val nuevo = PoliticaAcceso.registrarFallo(acceso, ahora)
                    db.usuarioDao().actualizar(usuario.copy(intentosFallidos = nuevo.intentosFallidos, bloqueadoHasta = nuevo.bloqueadoHasta))
                    _estado.value = if (PoliticaAcceso.estaBloqueado(nuevo, ahora)) {
                        LoginEstado.Bloqueado(PoliticaAcceso.segundosRestantes(nuevo, ahora))
                    } else {
                        LoginEstado.Error("PIN incorrecto")
                    }
                }
            } catch (error: Exception) {
                _estado.value = LoginEstado.Error(error.message ?: "Error al verificar PIN")
            }
        }
    }

    fun reiniciar() {
        if (_estado.value !is LoginEstado.Verificando) _estado.value = LoginEstado.Inicial
    }
}
