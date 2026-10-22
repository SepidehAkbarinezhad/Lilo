package com.sepideh.lilo.password.domain.crypto

import kotlinx.coroutines.flow.StateFlow

/** Authentication and key storage stay in platform implementations. */
interface VaultCrypto {
    val session: StateFlow<VaultSession>
    val unlocked: Boolean get() = session.value.unlocked
    val generation: Long get() = session.value.generation
    fun lock()
    suspend fun seal(plaintext: ByteArray, recordId: String): SealedPassword
    suspend fun open(record: SealedPassword, recordId: String): ByteArray
}
