package com.mypes.vinto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.DetalleVenta
import com.mypes.vinto.data.MovimientoCaja
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.OutboxEntry
import com.mypes.vinto.data.Venta
import com.mypes.vinto.sync.SyncScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

data class VentaFormulario(
    val productoId: String = "",
    val nombreProducto: String = "",
    val cantidad: Double = 0.0,
    val precioUnitario: Double = 0.0,
    val formaPago: String = "CONTADO",
    val clienteFiado: String? = null,
    val guardando: Boolean = false,
    val exito: Boolean = false,
    val error: String? = null
) {
    val total: Double
        get() = cantidad * precioUnitario

    val esValido: Boolean
        get() = productoId.isNotBlank()
            && cantidad > 0.0
            && precioUnitario >= 0.0
            && formaPago in setOf("CONTADO", "FIADO")
            && (formaPago != "FIADO" || !clienteFiado.isNullOrBlank())
}

class VentaViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MypesDatabase.obtener(application)
    private val _form = MutableStateFlow(VentaFormulario())
    val form: StateFlow<VentaFormulario> = _form

    fun actualizarFormulario(formulario: VentaFormulario) {
        _form.value = formulario.copy(error = null, exito = false)
    }

    fun guardarVenta() {
        val estado = _form.value
        if (!estado.esValido || estado.guardando) return

        viewModelScope.launch {
            _form.value = estado.copy(guardando = true, error = null)
            try {
                val ventaId = UUID.randomUUID().toString()
                val ahora = System.currentTimeMillis()
                val venta = Venta(
                    id = ventaId,
                    total = estado.total,
                    formaPago = estado.formaPago,
                    clienteFiado = estado.clienteFiado,
                    creadoEn = ahora
                )
                val detalle = DetalleVenta(
                    ventaId = ventaId,
                    productoId = estado.productoId,
                    cantidad = estado.cantidad,
                    precioUnitario = estado.precioUnitario
                )
                val movimiento = MovimientoCaja(
                    tipo = if (estado.formaPago == "CONTADO") "INGRESO" else "PENDIENTE",
                    monto = estado.total,
                    concepto = "Venta de ${estado.nombreProducto} x${estado.cantidad}",
                    ventaId = ventaId,
                    creadoEn = ahora
                )
                val payload = JSONObject().apply {
                    put("ventaId", ventaId)
                    put("productoId", estado.productoId)
                    put("cantidad", estado.cantidad)
                    put("precioUnitario", estado.precioUnitario)
                    put("total", estado.total)
                    put("formaPago", estado.formaPago)
                    put("clienteFiado", estado.clienteFiado ?: JSONObject.NULL)
                    put("creadoEn", ahora)
                }.toString()
                val outbox = OutboxEntry(tipo = "VENTA", payloadJson = payload)

                db.ventaDao().registrarVentaCompleta(venta, listOf(detalle), movimiento, outbox)
                db.productoDao().ajustarStock(estado.productoId, -estado.cantidad, ahora)
                SyncScheduler.programarSync(getApplication())
                _form.value = estado.copy(guardando = false, exito = true)
            } catch (error: Exception) {
                _form.value = estado.copy(
                    guardando = false,
                    error = error.message ?: "Error al guardar la venta"
                )
            }
        }
    }
}
