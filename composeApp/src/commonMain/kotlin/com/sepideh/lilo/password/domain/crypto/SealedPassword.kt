package com.sepideh.lilo.password.domain.crypto

data class SealedPassword(val format: Int, val nonce: ByteArray, val ciphertext: ByteArray, val wrappedKey: ByteArray = byteArrayOf())
