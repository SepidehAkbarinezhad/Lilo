package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import com.sepideh.lilo.password.domain.crypto.SealedPassword

import kotlin.test.*

class PasswordEncryptionTest {
    private val key = ByteArray(32) { it.toByte() }
    private val wrappedKey = ByteArray(256) // Cipher-only tests; RSA round-trip is tested separately.
    private val aad = "lilo-password-v1:record".encodeToByteArray()
    @Test fun roundTripAndFreshNonce() {
        val input = "secret فارسی".encodeToByteArray()
        val first = AndroidPasswordCipher.encrypt(key, input, aad, wrappedKey)
        val second = AndroidPasswordCipher.encrypt(key, input, aad, wrappedKey)
        assertEquals(PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP, first.format)
        assertContentEquals(input, AndroidPasswordCipher.decrypt(key, first, aad))
        assertFalse(first.nonce.contentEquals(second.nonce))
        assertFalse(first.ciphertext.contentEquals(second.ciphertext))
    }
    @Test fun rejectsTamperingAndWrongIdentityOrKey() {
        val encrypted = AndroidPasswordCipher.encrypt(key, "private".encodeToByteArray(), aad, wrappedKey)
        for (index in listOf(0, encrypted.ciphertext.lastIndex)) {
            val damaged = encrypted.ciphertext.copyOf().apply { this[index] = (this[index].toInt() xor 1).toByte() }
            assertFails { AndroidPasswordCipher.decrypt(key, encrypted.copy(ciphertext = damaged), aad) }
        }
        val nonce = encrypted.nonce.copyOf().apply { this[0] = (this[0].toInt() xor 1).toByte() }
        assertFails { AndroidPasswordCipher.decrypt(key, encrypted.copy(nonce = nonce), aad) }
        assertFails { AndroidPasswordCipher.decrypt(key, encrypted, "other-row".encodeToByteArray()) }
        assertFails { AndroidPasswordCipher.decrypt(ByteArray(32), encrypted, aad) }
        for (format in listOf(PasswordEncryptionFormat.IOS_AES_CBC_HMAC_RSA_OAEP, 3, 4, 0, -1, Int.MAX_VALUE)) {
            assertFailsWith<IllegalArgumentException> { AndroidPasswordCipher.decrypt(key, encrypted.copy(format = format), aad) }
        }
        for (malformed in listOf(
            encrypted.copy(wrappedKey = byteArrayOf()), // Obsolete whole-vault envelope, despite reused ID.
            encrypted.copy(wrappedKey = ByteArray(255)), encrypted.copy(wrappedKey = ByteArray(257)),
            encrypted.copy(nonce = ByteArray(11)), encrypted.copy(nonce = ByteArray(13)),
            encrypted.copy(ciphertext = ByteArray(15)),
        )) {
            assertFailsWith<IllegalArgumentException> { AndroidPasswordCipher.validateRecord(malformed) }
            assertFailsWith<IllegalArgumentException> { AndroidPasswordCipher.decrypt(key, malformed, aad) }
        }
        assertFailsWith<IllegalArgumentException> { AndroidPasswordCipher.decrypt(ByteArray(16), encrypted, aad) }
    }
    @Test fun publicKeySaveUsesIndependentKeysAndBindsCiphertextToRecord() {
        val pair = java.security.KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val first = AndroidPasswordCipher.seal(pair.public, "first secret".encodeToByteArray(), "one")
        val second = AndroidPasswordCipher.seal(pair.public, "second secret".encodeToByteArray(), "two")
        fun unwrap(record: SealedPassword): ByteArray = javax.crypto.Cipher.getInstance("RSA/ECB/OAEPPadding").run {
            init(javax.crypto.Cipher.DECRYPT_MODE, pair.private, AndroidPasswordCipher.oaep)
            doFinal(record.wrappedKey)
        }
        val firstKey = unwrap(first)
        val secondKey = unwrap(second)
        try {
            assertEquals(PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP, first.format)
            assertEquals(256, first.wrappedKey.size)
            assertFalse(firstKey.contentEquals(secondKey))
            assertFails { unwrap(first.copy(wrappedKey = first.wrappedKey.copyOf().apply { this[0] = (this[0].toInt() xor 1).toByte() })) }
            assertFails { AndroidPasswordCipher.seal(pair.public, byteArrayOf(1), " ") }
            assertEquals("first secret", AndroidPasswordCipher.decrypt(firstKey, first, "lilo-password-v1:one".encodeToByteArray()).decodeToString())
            assertFails { AndroidPasswordCipher.decrypt(firstKey, second, "lilo-password-v1:two".encodeToByteArray()) }
            assertFails { AndroidPasswordCipher.decrypt(firstKey, first, "lilo-password-v1:two".encodeToByteArray()) }
        } finally { firstKey.fill(0); secondKey.fill(0) }
    }

}
