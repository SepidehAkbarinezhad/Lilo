package com.sepideh.lilo.password.domain

import kotlinx.coroutines.flow.StateFlow

data class VaultSession(val generation: Long = 0, val unlocked: Boolean = false, val recordId: String? = null)

/** Authentication and key storage stay in platform implementations. */
interface VaultCrypto {
    val session: StateFlow<VaultSession>
    val unlocked: Boolean get() = session.value.unlocked
    val generation: Long get() = session.value.generation
    fun lock()
    suspend fun seal(plaintext: ByteArray, recordId: String): SealedPassword
    suspend fun open(record: SealedPassword, recordId: String): ByteArray
}

interface PasswordRepository {
    fun observePasswords(): kotlinx.coroutines.flow.Flow<List<Password>>
    suspend fun unlockRequest(id: String): PasswordUnlockRequest
    suspend fun get(id: String): Password?
    suspend fun save(password: Password): Password
    suspend fun delete(id: String)
}

fun List<Password>.matching(query: String, alphabetical: Boolean): List<Password> {
    val text = query.trim().lowercase().replace('ي', 'ی').replace('ك', 'ک')
    fun normalized(value: String) = value.lowercase().replace('ي', 'ی').replace('ك', 'ک')
    val matches = filter { text.isEmpty() || normalized(it.displayTitle).contains(text) || normalized(it.serviceName).contains(text) || normalized(it.username).contains(text) || normalized(it.website).contains(text) }
    return if (alphabetical) matches.sortedWith(compareBy<Password> { normalized(it.displayTitle) }.thenBy { it.id })
    else matches.sortedWith(compareByDescending<Password> { it.updatedAt }.thenBy { it.id })
}
