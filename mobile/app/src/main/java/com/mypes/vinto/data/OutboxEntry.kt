package com.mypes.vinto.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "outbox",
    indices = [Index(value = ["operationId"], unique = true)]
)
data class OutboxEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tipo: String,
    val operationId: String = UUID.randomUUID().toString(),
    val schemaVersion: Int = 1,
    val payloadJson: String,
    val estado: String = "PENDIENTE",
    val intentos: Int = 0,
    val creadoEn: Long = System.currentTimeMillis()
)
