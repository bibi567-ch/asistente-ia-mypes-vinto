package com.mypes.vinto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OutboxDao {
    @Insert
    suspend fun insertar(entry: OutboxEntry)

    @Query("SELECT * FROM outbox WHERE estado = 'PENDIENTE' ORDER BY creadoEn ASC LIMIT :limit")
    suspend fun pendientes(limit: Int = 100): List<OutboxEntry>

    @Query("SELECT * FROM outbox WHERE estado = 'PENDIENTE' ORDER BY creadoEn ASC")
    fun observarPendientes(): Flow<List<OutboxEntry>>

    @Query("UPDATE outbox SET estado = 'SYNC' WHERE operationId = :operationId")
    suspend fun marcarSincronizada(operationId: String)

    @Query("UPDATE outbox SET estado = 'ERROR', intentos = intentos + 1 WHERE operationId = :operationId")
    suspend fun marcarError(operationId: String)

    @Query("UPDATE outbox SET estado = 'ERROR', intentos = intentos + 1 WHERE id = :id")
    suspend fun marcarErrorPorId(id: String)

    @Query("DELETE FROM outbox WHERE estado = 'SYNC'")
    suspend fun limpiarSincronizadas()
}
