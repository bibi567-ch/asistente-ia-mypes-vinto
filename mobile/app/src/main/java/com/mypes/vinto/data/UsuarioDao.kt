package com.mypes.vinto.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE id = 1 LIMIT 1")
    suspend fun obtener(): Usuario?

    @Query(
        """SELECT * FROM usuarios
            WHERE lower(trim(coalesce(carnet, ''))) = lower(trim(:identificador))
               OR lower(trim(coalesce(celular, ''))) = lower(trim(:identificador))
               OR lower(trim(coalesce(email, ''))) = lower(trim(:identificador))
            LIMIT 1"""
    )
    suspend fun buscarPorIdentificador(identificador: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: Usuario)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun actualizar(usuario: Usuario)

    @Query("UPDATE usuarios SET nombreNegocio = :nombreNegocio, rubro = :rubro WHERE id = 1")
    suspend fun actualizarNegocio(nombreNegocio: String, rubro: String): Int
}
