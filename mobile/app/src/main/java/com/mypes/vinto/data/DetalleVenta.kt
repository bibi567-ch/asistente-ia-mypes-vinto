package com.mypes.vinto.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "detalle_ventas",
    foreignKeys = [
        ForeignKey(entity = Venta::class, parentColumns = ["id"], childColumns = ["ventaId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Producto::class, parentColumns = ["id"], childColumns = ["productoId"], onDelete = ForeignKey.RESTRICT)
    ],
    indices = [Index("ventaId"), Index("productoId")]
)
data class DetalleVenta(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val ventaId: String,
    val productoId: String,
    val cantidad: Double,
    val precioUnitario: Double
)
