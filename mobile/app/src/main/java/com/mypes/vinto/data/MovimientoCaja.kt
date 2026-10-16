package com.mypes.vinto.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "movimientos_caja")
data class MovimientoCaja(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tipo: String,
    val monto: Double,
    val concepto: String,
    val ventaId: String? = null,
    val creadoEn: Long = System.currentTimeMillis()
)
