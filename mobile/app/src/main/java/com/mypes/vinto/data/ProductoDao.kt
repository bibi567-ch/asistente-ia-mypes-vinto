package com.mypes.vinto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(producto: Producto)

    @Update
    suspend fun actualizar(producto: Producto)

    @Delete
    suspend fun eliminar(producto: Producto)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarSiNoExiste(producto: Producto): Long

    @Query("SELECT EXISTS(SELECT 1 FROM productos WHERE nombre = :nombre COLLATE NOCASE)")
    suspend fun existeNombre(nombre: String): Boolean

    @Transaction
    suspend fun insertarPlantillaSiNoExiste(producto: Producto): Boolean {
        if (existeNombre(producto.nombre)) return false
        return insertarSiNoExiste(producto) != -1L
    }

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
