package com.mypes.vinto.data

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PinHasher {
    private const val LONGITUD_SALT = 16
    private const val ITERACIONES_POR_DEFECTO = 120_000

    data class PinHash(val hash: String, val salt: String, val iteraciones: Int)

    fun crear(pin: String): PinHash {
        val salt = ByteArray(LONGITUD_SALT).also(SecureRandom()::nextBytes)
        val iteraciones = ITERACIONES_POR_DEFECTO
        return PinHash(
            hash = derivar(pin, salt, iteraciones),
            salt = Base64.encodeToString(salt, Base64.NO_WRAP),
            iteraciones = iteraciones
        )
    }

    fun verificar(pin: String, esperado: PinHash): Boolean {
        val salt = Base64.decode(esperado.salt, Base64.NO_WRAP)
        val calculado = derivar(pin, salt, esperado.iteraciones)
        return calculado == esperado.hash
    }

    private fun derivar(pin: String, salt: ByteArray, iteraciones: Int): String {
        val spec = PBEKeySpec(pin.toCharArray(), salt, iteraciones, 256)
        return try {
            val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).encoded
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } finally {
            spec.clearPassword()
        }
    }
}
