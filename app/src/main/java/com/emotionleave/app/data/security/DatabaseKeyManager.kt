package com.emotionleave.app.data.security

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class DatabaseKeyManager(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        DATABASE_KEY_PREFS,
        Context.MODE_PRIVATE,
    )

    fun getOrCreatePassphrase(): ByteArray {
        val encrypted = preferences.getString(KEY_ENCRYPTED_PASSPHRASE, null)
        val iv = preferences.getString(KEY_IV, null)
        if (encrypted != null && iv != null) {
            return decrypt(
                encrypted = encrypted.hexToBytes(),
                iv = iv.hexToBytes(),
            )
        }

        val passphrase = ByteArray(DATABASE_PASSPHRASE_BYTES)
        SecureRandom().nextBytes(passphrase)
        val encryptedPassphrase = encrypt(passphrase)
        preferences.edit()
            .putString(KEY_ENCRYPTED_PASSPHRASE, encryptedPassphrase.cipherText.toHex())
            .putString(KEY_IV, encryptedPassphrase.iv.toHex())
            .apply()
        return passphrase
    }

    fun clearPassphrase() {
        preferences.edit().clear().apply()
    }

    private fun encrypt(passphrase: ByteArray): EncryptedValue {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        return EncryptedValue(
            cipherText = cipher.doFinal(passphrase),
            iv = cipher.iv,
        )
    }

    private fun decrypt(encrypted: ByteArray, iv: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(),
            GCMParameterSpec(GCM_TAG_BITS, iv),
        )
        return cipher.doFinal(encrypted)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        (keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry)?.let {
            return it.secretKey
        }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE,
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setRandomizedEncryptionRequired(true)
            .build()
        generator.init(spec)
        return generator.generateKey()
    }

    private data class EncryptedValue(
        val cipherText: ByteArray,
        val iv: ByteArray,
    )

    private fun ByteArray.toHex(): String = joinToString(separator = "") { byte ->
        "%02x".format(byte)
    }

    private fun String.hexToBytes(): ByteArray =
        chunked(2).map { it.toInt(16).toByte() }.toByteArray()

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val DATABASE_KEY_PREFS = "emotionleave_database_key"
        const val DATABASE_PASSPHRASE_BYTES = 32
        const val GCM_TAG_BITS = 128
        const val KEY_ALIAS = "emotionleave_room_database_key"
        const val KEY_ENCRYPTED_PASSPHRASE = "encrypted_passphrase"
        const val KEY_IV = "iv"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
