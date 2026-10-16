package com.mypes.vinto.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "ventas")
data class Venta(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val total: Double,
    val formaPago: String,
    val clienteFiado: String? = null,
    val creadoEn: Long = System.currentTimeMillis()
)
