package com.mypes.vinto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.Producto
import com.mypes.vinto.nlu.AsistenteVozFacade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardEstado(
    val totalVentas: Double = 0.0,
    val totalProductos: Int = 0,
    val alertas: Int = 0,
    val productosBajoStock: List<Producto> = emptyList(),
    val escuchando: Boolean = false,
    val textoParcial: String = "",
    val ultimoResultado: AsistenteVozFacade.Resultado? = null,
    val mensajeError: String? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MypesDatabase.obtener(application)
    private val asistente = AsistenteVozFacade(application)
    private val _estado = MutableStateFlow(DashboardEstado())
    val estado: StateFlow<DashboardEstado> = _estado

    init {
        asistente.inicializar()
        cargarMetricas()
    }

    fun cargarMetricas() {
        viewModelScope.launch {
            db.productoDao().observarTodos().collect { productos ->
                _estado.value = _estado.value.copy(
                    totalProductos = productos.size,
                    productosBajoStock = productos.filter { it.stock < 5.0 },
                    alertas = productos.count { it.stock <= 0.0 }
                )
            }
        }
    }

    fun iniciarEscucha() {
        _estado.value = _estado.value.copy(escuchando = true, textoParcial = "", mensajeError = null)
        asistente.escuchar(
            onParcial = { texto -> _estado.value = _estado.value.copy(textoParcial = texto) },
            onResultado = { resultado ->
                _estado.value = _estado.value.copy(escuchando = false, textoParcial = "", ultimoResultado = resultado)
            },
            onError = { mensaje ->
                _estado.value = _estado.value.copy(escuchando = false, textoParcial = "", mensajeError = mensaje)
            }
        )
    }

    fun detenerEscucha() {
        asistente.detener()
        _estado.value = _estado.value.copy(escuchando = false, textoParcial = "")
    }

    override fun onCleared() {
        asistente.close()
        super.onCleared()
    }
}
