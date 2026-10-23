package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.SealedPassword
import com.sepideh.lilo.password.domain.crypto.VaultCrypto
import com.sepideh.lilo.password.domain.crypto.VaultLockedException
import com.sepideh.lilo.password.domain.crypto.VaultSession

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Only a transient session owns the unwrapped key. Never persisted as plaintext. */
abstract class SessionVaultCrypto : VaultCrypto {
    private data class Session(val key: ByteArray, val generation: Long, val recordId: String?)
    private val keySession = MutableStateFlow<Session?>(null)
    private val currentSession = MutableStateFlow(VaultSession())
    final override val session: StateFlow<VaultSession> = currentSession

    protected fun acceptAuthenticatedKey(key: ByteArray, recordId: String? = null) {
        lock()
        val generation = currentSession.value.generation + 1
        keySession.value = Session(key.copyOf(), generation, recordId)
        key.fill(0)
        currentSession.value = VaultSession(generation, unlocked = true, recordId = recordId)
    }

    final override fun lock() {
        val previous = keySession.value
        keySession.value = null
        previous?.key?.fill(0)
        currentSession.value = VaultSession(currentSession.value.generation + 1)
    }

    private suspend fun <T> withKey(recordId: String, operation: suspend (ByteArray) -> T): T {
        val current = keySession.value ?: throw VaultLockedException()
        if (current.recordId != null && current.recordId != recordId) throw VaultLockedException()
        val copy = current.key.copyOf()
        try {
            if (keySession.value !== current) throw VaultLockedException()
            val result = operation(copy)
            if (keySession.value !== current) {
                if (result is ByteArray) result.fill(0)
                throw VaultLockedException()
            }
            return result
        } finally { copy.fill(0) }
    }

    // Saving uses a fresh record key and the platform's public key, never a session key.
    abstract override suspend fun seal(plaintext: ByteArray, recordId: String): SealedPassword
    final override suspend fun open(record: SealedPassword, recordId: String): ByteArray =
        withKey(recordId) { decrypt(it, record, aad(recordId)) }

    protected abstract suspend fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray
    private fun aad(id: String): ByteArray {
        require(id.isNotBlank())
        return "lilo-password-v1:$id".encodeToByteArray()
    }
}
