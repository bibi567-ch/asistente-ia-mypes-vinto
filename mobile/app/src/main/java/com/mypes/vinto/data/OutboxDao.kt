package com.mypes.vinto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OutboxDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(entry: OutboxEntry)

    @Query("SELECT * FROM outbox_entries ORDER BY timestamp DESC LIMIT 50")
    suspend fun recientes(): List<OutboxEntry>

    @Query("SELECT * FROM outbox_entries WHERE sincronizado = 0 ORDER BY timestamp ASC LIMIT 50")
    suspend fun pendientes(): List<OutboxEntry>

    @Query("UPDATE outbox_entries SET confirmado = :confirmado, ejecutado = :ejecutado WHERE id = :id")
    suspend fun actualizarResultado(id: String, confirmado: Boolean, ejecutado: Boolean)

    @Query("UPDATE outbox_entries SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarSincronizado(id: String)
}
