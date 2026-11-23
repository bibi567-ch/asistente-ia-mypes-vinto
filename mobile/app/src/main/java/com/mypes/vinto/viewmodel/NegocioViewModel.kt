package com.mypes.vinto.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mypes.vinto.data.MovimientoCaja
import com.mypes.vinto.data.MypesDatabase
import com.mypes.vinto.data.Producto
import com.mypes.vinto.data.RankingProducto
import com.mypes.vinto.data.Venta
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Datos del negocio para las pantallas del prototipo Figma (Inicio, Ventas, Caja, Análisis).
 * Lee la misma base Room cifrada (SQLCipher) que el resto de la app; no cambia el esquema.
 */
class NegocioViewModel(application: Application) : AndroidViewModel(application) {
    private val db = MypesDatabase.obtener(application)

    val ventas: StateFlow<List<Venta>> =
        db.ventaDao().observarTodas().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val movimientos: StateFlow<List<MovimientoCaja>> =
        db.ventaDao().observarMovimientos().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val productos: StateFlow<List<Producto>> =
        db.productoDao().observarTodos().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val ranking: StateFlow<List<RankingProducto>> =
        db.ventaDao().observarRanking().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** Compra de mercadería o gasto del día: sale de caja. */
    fun registrarEgreso(concepto: String, monto: Double) {
        if (concepto.isBlank() || !monto.isFinite() || monto <= 0.0) return
        viewModelScope.launch {
            db.ventaDao().insertarMovimiento(MovimientoCaja(tipo = "EGRESO", monto = monto, concepto = concepto.trim()))
        }
    }

    companion object {
        fun inicioDelDia(diasAtras: Int = 0): Long = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -diasAtras)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}

/** Resumen de caja del día (misma lógica que la pantalla Caja del Figma). */
data class ResumenCaja(
    val ventasTotales: Double,
    val cantidadVentas: Int,
    val contado: Double,
    val fiado: Double,
    val cobros: Double,
    val egresos: Double
) {
    val cajaEstimada: Double get() = contado + cobros - egresos

    companion object {
        fun de(ventas: List<Venta>, movimientos: List<MovimientoCaja>, desde: Long): ResumenCaja {
            val hoy = ventas.filter { it.creadoEn >= desde }
            val movHoy = movimientos.filter { it.creadoEn >= desde }
            return ResumenCaja(
                ventasTotales = hoy.sumOf { it.total },
                cantidadVentas = hoy.size,
                contado = hoy.filter { it.formaPago != "FIADO" }.sumOf { it.total },
                fiado = hoy.filter { it.formaPago == "FIADO" }.sumOf { it.total },
                cobros = movHoy.filter { it.tipo == "INGRESO" && it.concepto.startsWith("Cobro") }.sumOf { it.monto },
                egresos = movHoy.filter { it.tipo == "EGRESO" }.sumOf { it.monto }
            )
        }
    }
}
