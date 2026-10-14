package com.mypes.vinto.data

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

data class PinHash(val hash: String, val salt: String, val iteraciones: Int)

object PinHasher {
    const val ITERACIONES = 100_000
    private const val ALGORITMO = "PBKDF2WithHmacSHA256"

    fun crear(pin: String, iteraciones: Int = ITERACIONES): PinHash {
        require(pin.length == 4 && pin.all { it.isDigit() }) { "El PIN debe tener 4 digitos" }
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = derivar(pin, salt, iteraciones)
        return PinHash(
            Base64.getEncoder().encodeToString(hash),
            Base64.getEncoder().encodeToString(salt),
            iteraciones
        )
    }

    fun verificar(pin: String, guardado: PinHash): Boolean {
        val esperado = Base64.getDecoder().decode(guardado.hash)
        val salt = Base64.getDecoder().decode(guardado.salt)
        val calculado = derivar(pin, salt, guardado.iteraciones)
        return MessageDigest.isEqual(esperado, calculado)
    }

    private fun derivar(pin: String, salt: ByteArray, iteraciones: Int): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, iteraciones, 256)
        return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).encoded
    }
}
