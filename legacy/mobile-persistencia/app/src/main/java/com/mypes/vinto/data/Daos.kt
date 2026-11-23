package com.mypes.vinto.data

import androidx.room.*

@Dao
interface UsuarioDao {
    @Insert suspend fun insertar(u: Usuario)
    @Update suspend fun actualizar(u: Usuario)
    @Query("SELECT * FROM usuarios LIMIT 1") suspend fun obtener(): Usuario?
}

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertar(p: Producto)
    @Query("SELECT * FROM productos") suspend fun todos(): List<Producto>
    @Query("SELECT * FROM productos WHERE id = :id") suspend fun porId(id: String): Producto?
    @Query("UPDATE productos SET stock = stock + :delta WHERE id = :id") suspend fun ajustarStock(id: String, delta: Double)
}

@Dao
interface VentaDao {
    @Insert suspend fun insertarVenta(v: Venta)
    @Insert suspend fun insertarDetalle(d: DetalleVenta)
}

@Dao
interface CajaDao {
    @Insert suspend fun insertar(m: MovimientoCaja)
    @Query("SELECT COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN monto ELSE -monto END), 0) FROM movimientos_caja")
    suspend fun saldo(): Double
}

@Dao
interface OutboxDao {
    @Insert suspend fun insertar(o: OutboxEntry)
    @Query("SELECT * FROM outbox WHERE estado = 'PENDIENTE' ORDER BY creadoEn") suspend fun pendientes(): List<OutboxEntry>
}

@Dao
interface AuditoriaDao {
    @Insert suspend fun insertar(a: AuditoriaAcceso)
    @Query("SELECT * FROM auditoria_acceso ORDER BY creadoEn DESC") suspend fun todos(): List<AuditoriaAcceso>
}
