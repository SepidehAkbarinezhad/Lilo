package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class IosPasswordCipherTest {
    @Test fun authenticatedRoundTripAndFreshIv() = runBlocking<Unit> {
        val key = ByteArray(64) { it.toByte() }
        val aad = "lilo-password-v1:record-1".encodeToByteArray()
        val wrappedKey = ByteArray(256) // Cipher-only test; no Keychain authentication simulated.
        val plain = "private فارسی".encodeToByteArray()
        val first = IosPasswordCipher.encrypt(key, plain, aad, wrappedKey)
        val second = IosPasswordCipher.encrypt(key, plain, aad, wrappedKey)
        assertEquals(PasswordEncryptionFormat.IOS_AES_CBC_HMAC_RSA_OAEP, first.format)
        assertContentEquals(plain, IosPasswordCipher.decrypt(key, first, aad))
        assertFalse(first.nonce.contentEquals(second.nonce))
        for (index in listOf(0, first.ciphertext.lastIndex)) {
            val damaged = first.ciphertext.copyOf().apply { this[index] = (this[index].toInt() xor 1).toByte() }
            assertFails { IosPasswordCipher.decrypt(key, first.copy(ciphertext = damaged), aad) }
        }
        val iv = first.nonce.copyOf().apply { this[0] = (this[0].toInt() xor 1).toByte() }
        assertFails { IosPasswordCipher.decrypt(key, first.copy(nonce = iv), aad) }
        assertFails { IosPasswordCipher.decrypt(key, first, "other-record".encodeToByteArray()) }
        assertFails { IosPasswordCipher.decrypt(ByteArray(64), first, aad) }
        for (format in listOf(PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP, 3, 4, 0, -1, Int.MAX_VALUE)) {
            assertFailsWith<IllegalArgumentException> { IosPasswordCipher.decrypt(key, first.copy(format = format), aad) }
        }
        for (malformed in listOf(
            first.copy(wrappedKey = byteArrayOf()), // Obsolete whole-vault envelope, despite reused ID.
            first.copy(wrappedKey = ByteArray(255)), first.copy(wrappedKey = ByteArray(257)),
            first.copy(nonce = ByteArray(15)), first.copy(nonce = ByteArray(17)),
            first.copy(ciphertext = ByteArray(32)), first.copy(ciphertext = ByteArray(47)),
            first.copy(ciphertext = ByteArray(49)),
        )) {
            assertFailsWith<IllegalArgumentException> { IosPasswordCipher.validateRecord(malformed) }
            assertFailsWith<IllegalArgumentException> { IosPasswordCipher.decrypt(key, malformed, aad) }
        }
        assertFailsWith<IllegalArgumentException> { IosPasswordCipher.decrypt(ByteArray(32), first, aad) }
    }
}
