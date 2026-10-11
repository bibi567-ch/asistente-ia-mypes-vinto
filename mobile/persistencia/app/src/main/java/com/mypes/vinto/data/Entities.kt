package com.mypes.vinto.data

import androidx.room.*
import java.util.UUID

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val pinHash: String,
    val salt: String,
    val iteraciones: Int,
    val creadoEn: Long = System.currentTimeMillis()
)

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val precio: Double,
    val stock: Double,
    val actualizadoEn: Long = System.currentTimeMillis()
)

@Entity(tableName = "ventas")
data class Venta(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val total: Double,
    val formaPago: String,
    val clienteFiado: String? = null,
    val creadoEn: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "detalle_ventas",
    foreignKeys = [
        ForeignKey(entity = Venta::class, parentColumns = ["id"], childColumns = ["ventaId"]),
        ForeignKey(entity = Producto::class, parentColumns = ["id"], childColumns = ["productoId"])
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

@Entity(tableName = "movimientos_caja")
data class MovimientoCaja(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tipo: String,
    val monto: Double,
    val concepto: String,
    val ventaId: String? = null,
    val creadoEn: Long = System.currentTimeMillis()
)

@Entity(tableName = "outbox")
data class OutboxEntry(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tipo: String,
    val payloadJson: String,
    val estado: String = "PENDIENTE",
    val intentos: Int = 0,
    val creadoEn: Long = System.currentTimeMillis()
)
