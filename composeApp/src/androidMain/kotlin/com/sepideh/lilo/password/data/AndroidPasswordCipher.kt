package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.SealedPassword
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

internal object AndroidPasswordCipher {
    val oaep = javax.crypto.spec.OAEPParameterSpec("SHA-256", "MGF1", java.security.spec.MGF1ParameterSpec.SHA1,
        javax.crypto.spec.PSource.PSpecified.DEFAULT)

    fun seal(publicKey: java.security.PublicKey, plaintext: ByteArray, recordId: String): SealedPassword {
        val key = ByteArray(32).also(java.security.SecureRandom()::nextBytes)
        try {
            val wrapper = Cipher.getInstance("RSA/ECB/OAEPPadding").apply { init(Cipher.ENCRYPT_MODE, publicKey, oaep) }
            return encrypt(key, plaintext, "lilo-password-v1:$recordId".encodeToByteArray())
                .copy(format = 3, wrappedKey = wrapper.doFinal(key))
        } finally { key.fill(0) }
    }

    fun encrypt(key: ByteArray, plaintext: ByteArray, aad: ByteArray): SealedPassword {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"))
        cipher.updateAAD(aad)
        return SealedPassword(1, cipher.iv, cipher.doFinal(plaintext))
    }
    fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray {
        require(record.format == 1 && record.nonce.size == 12 && record.ciphertext.size >= 16)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, record.nonce))
        cipher.updateAAD(aad)
        return cipher.doFinal(record.ciphertext)
    }
}
