package com.mypes.vinto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class InventarioViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = MypesDatabase.obtener(application).productoDao()
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    init {
        viewModelScope.launch {
            dao.observarTodos().collect { _productos.value = it }
        }
    }

    fun agregarProducto(nombre: String, precio: Double, stock: Double) {
        viewModelScope.launch {
            dao.insertar(Producto(UUID.randomUUID().toString(), nombre.trim(), precio, stock))
        }
    }

    fun ajustarStock(id: String, delta: Double) {
        viewModelScope.launch { dao.ajustarStock(id, delta) }
    }
}
