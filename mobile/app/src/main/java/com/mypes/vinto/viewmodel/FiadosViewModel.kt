package com.mypes.vinto.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.MovimientoCaja
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.OutboxEntry
import com.mypes.vinto.data.Venta
import com.mypes.vinto.network.dto.TransaccionPayload
import com.mypes.vinto.sync.SyncScheduler
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

class FiadosViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MypesDatabase.obtener(application)
    private val _fiados = MutableStateFlow<List<Venta>>(emptyList())
    val fiados: StateFlow<List<Venta>> = _fiados.asStateFlow()
    private val _cobroEnCurso = MutableStateFlow<String?>(null)
    val cobroEnCurso: StateFlow<String?> = _cobroEnCurso.asStateFlow()
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        viewModelScope.launch {
            database.ventaDao().observarFiadosPendientes().collect { _fiados.value = it }
        }
    }

    fun cobrarDeuda(ventaId: String) {
        if (_cobroEnCurso.value != null) return

        viewModelScope.launch {
            _cobroEnCurso.value = ventaId
            _mensaje.value = null
            try {
                val dao = database.ventaDao()
                val venta = dao.obtenerVenta(ventaId)
                    ?.takeIf { it.formaPago == "FIADO" && !it.pagado }
                    ?: error("La deuda ya fue cobrada o no existe.")
                val detalle = dao.obtenerDetallePrincipal(ventaId)
                    ?: error("La venta no tiene detalle de producto; no se puede sincronizar el cobro.")
                val ahora = System.currentTimeMillis()
                val cliente = venta.clienteFiado?.takeIf(String::isNotBlank) ?: "Cliente"
                val operationId = UUID.randomUUID().toString()
                val payload = TransaccionPayload(
                    ventaId = venta.id,
                    productoId = detalle.productoId,
                    cantidad = detalle.cantidad,
                    precioUnitario = detalle.precioUnitario,
                    total = venta.total,
                    formaPago = "CONTADO",
                    clienteFiado = venta.clienteFiado,
                    creadoEn = ahora
                )
                val outbox = OutboxEntry(
                    operationId = operationId,
                    tipo = "COBRO",
                    payloadJson = JSONObject().apply {
                        put("ventaId", payload.ventaId)
                        put("productoId", payload.productoId)
                        put("cantidad", payload.cantidad)
                        put("precioUnitario", payload.precioUnitario)
                        put("total", payload.total)
                        put("formaPago", payload.formaPago)
                        put("clienteFiado", payload.clienteFiado ?: JSONObject.NULL)
                        put("creadoEn", payload.creadoEn)
                    }.toString(),
                    creadoEn = ahora
                )
                val movimiento = MovimientoCaja(
                    tipo = "INGRESO",
                    monto = venta.total,
                    concepto = "Cobro de fiado de $cliente",
                    ventaId = venta.id,
                    creadoEn = ahora
                )

                dao.registrarCobroCompleto(venta.id, movimiento, outbox)
                try {
                    SyncScheduler.programarSync(getApplication())
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    Log.e("FiadosViewModel", "El cobro se guardó localmente, pero no se pudo programar la sincronización.", error)
                    _mensaje.value = "Cobro guardado localmente; no se pudo programar su sincronización."
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Log.e("FiadosViewModel", "No se pudo registrar el cobro de la deuda $ventaId.", error)
                _mensaje.value = error.message ?: "No se pudo registrar el cobro."
            } finally {
                _cobroEnCurso.value = null
            }
        }
    }
}
