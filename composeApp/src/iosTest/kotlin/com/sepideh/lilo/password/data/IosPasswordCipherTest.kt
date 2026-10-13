package com.sepideh.lilo.password.data

import kotlinx.coroutines.runBlocking
import kotlin.test.*

class IosPasswordCipherTest {
    @Test fun authenticatedRoundTripAndFreshIv() = runBlocking<Unit> {
        val key = ByteArray(64) { it.toByte() }
        val aad = "record-1".encodeToByteArray()
        val plain = "private فارسی".encodeToByteArray()
        val first = IosPasswordCipher.encrypt(key, plain, aad)
        val second = IosPasswordCipher.encrypt(key, plain, aad)
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
        assertFails { IosPasswordCipher.decrypt(key, first.copy(format = 1), aad) }
    }
}
