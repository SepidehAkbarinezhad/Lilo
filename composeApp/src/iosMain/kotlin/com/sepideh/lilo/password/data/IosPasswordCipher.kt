package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import com.sepideh.lilo.password.domain.crypto.SealedPassword
import kotlinx.cinterop.*
import kotlinx.coroutines.*
import platform.CoreCrypto.*
import platform.Security.*

@OptIn(ExperimentalForeignApi::class)
internal object IosPasswordCipher {
    // CommonCrypto's public KMP APIs provide AES-CBC and HMAC. Encrypt-then-MAC authenticates
    // the versioned record ID, IV and ciphertext before any plaintext is decrypted.
    suspend fun encrypt(key: ByteArray, plaintext: ByteArray, aad: ByteArray, wrappedKey: ByteArray): SealedPassword = withContext(Dispatchers.Default) {
        require(key.size == 64)
        require(wrappedKey.size == 256)
        val iv = randomBytes(16)
        val cipher = aes(kCCEncrypt, key, iv, plaintext)
        SealedPassword(PasswordEncryptionFormat.IOS_AES_CBC_HMAC_RSA_OAEP, iv, cipher + mac(key, aad + iv + cipher), wrappedKey)
    }
    fun validateRecord(record: SealedPassword) {
        require(record.format == PasswordEncryptionFormat.IOS_AES_CBC_HMAC_RSA_OAEP) { "Unsupported password format" }
        require(record.wrappedKey.size == 256 && record.nonce.size == 16 &&
            record.ciphertext.size >= 48 && (record.ciphertext.size - 32) % 16 == 0) {
            "Malformed password envelope"
        }
    }

    suspend fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray = withContext(Dispatchers.Default) {
        require(key.size == 64)
        validateRecord(record)
        val cipher = record.ciphertext.copyOfRange(0, record.ciphertext.size - 32)
        val expected = mac(key, aad + record.nonce + cipher)
        val actual = record.ciphertext.copyOfRange(cipher.size, record.ciphertext.size)
        var difference = 0
        for (i in expected.indices) difference = difference or (expected[i].toInt() xor actual[i].toInt())
        require(difference == 0) { "Invalid credential" }
        aes(kCCDecrypt, key, record.nonce, cipher)
    }
    private fun aes(operation: CCOperation, key: ByteArray, iv: ByteArray, input: ByteArray): ByteArray = memScoped {
        val output = ByteArray(input.size + 16)
        val written = alloc<ULongVar>()
        val status = key.usePinned { k -> iv.usePinned { v -> input.usePinned { data -> output.usePinned { out ->
            CCCrypt(operation, kCCAlgorithmAES, kCCOptionPKCS7Padding, k.addressOf(0), 32uL, v.addressOf(0),
                if (input.isEmpty()) null else data.addressOf(0), input.size.toULong(), out.addressOf(0), output.size.toULong(), written.ptr)
        } } } }
        try {
            check(status == kCCSuccess)
            output.copyOf(written.value.toInt())
        } finally { output.fill(0) }
    }
    private fun mac(key: ByteArray, data: ByteArray): ByteArray {
        val result = ByteArray(32)
        key.usePinned { k -> data.usePinned { d -> result.usePinned { out ->
            CCHmac(kCCHmacAlgSHA256, k.addressOf(32), 32uL, d.addressOf(0), data.size.toULong(), out.addressOf(0))
        } } }
        return result
    }
    private fun randomBytes(size: Int): ByteArray = ByteArray(size).also { data ->
        data.usePinned { check(SecRandomCopyBytes(kSecRandomDefault, size.toULong(), it.addressOf(0)) == errSecSuccess) }
    }
}
