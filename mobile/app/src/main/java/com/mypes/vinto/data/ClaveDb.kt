package com.mypes.vinto.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object ClaveDb {
    private const val ALIAS = "mypes_db_key"
    private const val PREFS = "secure_prefs"

    fun obtener(context: Context): ByteArray {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val guardada = prefs.getString("db_key", null)
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

        if (!ks.containsAlias(ALIAS)) {
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
                init(
                    KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build()
                )
            }.generateKey()
        }
        val kek = ks.getKey(ALIAS, null) as SecretKey
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        if (guardada == null) {
            val clave = ByteArray(32).also { SecureRandom().nextBytes(it) }
            cipher.init(Cipher.ENCRYPT_MODE, kek)
            val cifrada = cipher.doFinal(clave)
            prefs.edit().putString("db_key", Base64.encodeToString(cipher.iv + cifrada, Base64.NO_WRAP)).apply()
            return clave
        }
        val datos = Base64.decode(guardada, Base64.NO_WRAP)
        cipher.init(Cipher.DECRYPT_MODE, kek, GCMParameterSpec(128, datos.copyOfRange(0, 12)))
        return cipher.doFinal(datos.copyOfRange(12, datos.size))
    }
}
