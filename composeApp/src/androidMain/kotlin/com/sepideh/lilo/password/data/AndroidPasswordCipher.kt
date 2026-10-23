package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import com.sepideh.lilo.password.domain.crypto.SealedPassword
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object AndroidPasswordCipher {
    val oaep = javax.crypto.spec.OAEPParameterSpec("SHA-256", "MGF1", java.security.spec.MGF1ParameterSpec.SHA1,
        javax.crypto.spec.PSource.PSpecified.DEFAULT)

    fun seal(publicKey: java.security.PublicKey, plaintext: ByteArray, recordId: String): SealedPassword {
        require(recordId.isNotBlank())
        val key = ByteArray(32).also(java.security.SecureRandom()::nextBytes)
        try {
            val wrapper = Cipher.getInstance("RSA/ECB/OAEPPadding").apply { init(Cipher.ENCRYPT_MODE, publicKey, oaep) }
            return encrypt(key, plaintext, "lilo-password-v1:$recordId".encodeToByteArray(), wrapper.doFinal(key))
        } finally { key.fill(0) }
    }

    fun encrypt(key: ByteArray, plaintext: ByteArray, aad: ByteArray, wrappedKey: ByteArray): SealedPassword {
        require(key.size == 32 && wrappedKey.size == 256)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"))
        cipher.updateAAD(aad)
        return SealedPassword(PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP, cipher.iv, cipher.doFinal(plaintext), wrappedKey)
    }
    fun validateRecord(record: SealedPassword) {
        require(record.format == PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP) { "Unsupported password format" }
        require(record.wrappedKey.size == 256 && record.nonce.size == 12 && record.ciphertext.size >= 16) {
            "Malformed password envelope"
        }
    }

    fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray {
        require(key.size == 32)
        validateRecord(record)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, record.nonce))
        cipher.updateAAD(aad)
        return cipher.doFinal(record.ciphertext)
    }
}
