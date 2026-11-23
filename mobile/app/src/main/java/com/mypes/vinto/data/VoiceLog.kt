package com.mypes.vinto.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.ColumnInfo
import java.util.UUID

@Entity(tableName = "voice_logs", indices = [Index(value = ["eventId"], unique = true)])
data class VoiceLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventId: String = UUID.randomUUID().toString(),
    val textoOriginal: String,
    val textoCorregido: String,
    val intencion: String,
    val fecha: Long = System.currentTimeMillis(),
    @ColumnInfo(defaultValue = "0") val consentToUpload: Boolean = false
)

@Dao
interface VoiceLogDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(log: VoiceLog)

    @Query("SELECT * FROM voice_logs WHERE consentToUpload = 1 ORDER BY fecha ASC LIMIT :limit")
    suspend fun pendientes(limit: Int = 100): List<VoiceLog>

    @Query("DELETE FROM voice_logs WHERE eventId IN (:eventIds)")
    suspend fun eliminarPorEventIds(eventIds: List<String>)
}
