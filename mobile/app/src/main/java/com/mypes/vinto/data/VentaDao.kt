package com.mypes.vinto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface VentaDao {
    @Insert
    suspend fun insertarVenta(venta: Venta)

    @Insert
    suspend fun insertarDetalles(detalles: List<DetalleVenta>)

    @Insert
    suspend fun insertarMovimiento(movimiento: MovimientoCaja)

    @Insert
    suspend fun insertarOutbox(outbox: OutboxEntry)

    @Query("SELECT * FROM ventas ORDER BY creadoEn DESC")
    fun observarTodas(): Flow<List<Venta>>

    @Query("SELECT COALESCE(SUM(total), 0) FROM ventas WHERE creadoEn >= :desde")
    suspend fun totalDesde(desde: Long): Double

    @Query("SELECT COUNT(*) FROM ventas WHERE creadoEn >= :desde")
    suspend fun contarDesde(desde: Long): Int

    @Query("SELECT COALESCE(SUM(monto), 0) FROM movimientos_caja WHERE tipo = 'INGRESO'")
    fun observarTotalCaja(): Flow<Double>

    @Transaction
    suspend fun registrarVentaCompleta(
        venta: Venta,
        detalles: List<DetalleVenta>,
        movimiento: MovimientoCaja,
        outbox: OutboxEntry
    ) {
        insertarVenta(venta)
        insertarDetalles(detalles)
        insertarMovimiento(movimiento)
        insertarOutbox(outbox)
    }
}
