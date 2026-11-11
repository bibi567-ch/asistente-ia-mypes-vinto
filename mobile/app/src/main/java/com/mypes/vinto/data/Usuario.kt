package com.mypes.vinto.data

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey val id: Long = 1,
    val nombre: String,
    @ColumnInfo(defaultValue = "''") val nombres: String = "",
    @ColumnInfo(defaultValue = "''") val apellidos: String = "",
    val carnet: String? = null,
    val expedido: String? = null,
    val fechaNacimiento: String? = null,
    val lugarNacimiento: String? = null,
    val celular: String? = null,
    val direccion: String? = null,
    val email: String? = null,
    val nombreNegocio: String? = null,
    val rubro: String? = null,
    val pinHash: String,
    val salt: String,
    val iteraciones: Int,
    val intentosFallidos: Int = 0,
    val bloqueadoHasta: Long = 0
)
