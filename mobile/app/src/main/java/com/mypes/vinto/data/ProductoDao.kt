package com.mypes.vinto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(producto: Producto)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSiNoExiste(producto: Producto)

    @Query("UPDATE productos SET nombre = :nombre, precio = :precio, actualizadoEn = :actualizadoEn WHERE id = :id")
    suspend fun actualizarDesdeServidor(id: String, nombre: String, precio: Double, actualizadoEn: Long)

    @Transaction
    suspend fun guardarProductoSincronizado(producto: Producto) {
        insertarSiNoExiste(producto)
        actualizarDesdeServidor(producto.id, producto.nombre, producto.precio, producto.actualizadoEn)
    }

    @Query("SELECT * FROM productos ORDER BY nombre")
    fun observarTodos(): Flow<List<Producto>>

    @Query("UPDATE productos SET stock = stock + :delta, actualizadoEn = :actualizadoEn WHERE id = :id")
    suspend fun ajustarStock(id: String, delta: Double, actualizadoEn: Long = System.currentTimeMillis())
}
