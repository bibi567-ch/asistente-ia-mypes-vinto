package com.mypes.vinto.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.UUID

@Entity(tableName = "outbox_entries")
data class OutboxEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val textoComando: String,
    val intencion: String,
    val timestamp: Long = System.currentTimeMillis(),
    val confirmado: Boolean = false,
    val ejecutado: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val sincronizado: Boolean = false
)
