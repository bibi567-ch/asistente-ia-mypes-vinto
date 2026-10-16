package com.mypes.vinto.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey val id: String,
    val nombre: String,
    val precio: Double,
    val stock: Double,
    val actualizadoEn: Long
)
