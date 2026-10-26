package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.SealedPassword

internal actual suspend fun encryptRepositoryFixture(key: ByteArray, secret: ByteArray, id: String): SealedPassword =
    AndroidPasswordCipher.encrypt(key.copyOf(32), secret, passwordAad(id), ByteArray(256))

internal actual suspend fun decryptRepositoryFixture(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray =
    AndroidPasswordCipher.decrypt(key.copyOf(32), record, aad)
