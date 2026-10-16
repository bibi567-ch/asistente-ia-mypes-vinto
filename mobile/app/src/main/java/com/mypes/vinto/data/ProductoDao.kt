package com.mypes.vinto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre")
    fun observarTodos(): Flow<List<Producto>>

    @Query("UPDATE productos SET stock = stock + :delta, actualizadoEn = :actualizadoEn WHERE id = :id")
    suspend fun ajustarStock(id: String, delta: Double, actualizadoEn: Long = System.currentTimeMillis())
}
