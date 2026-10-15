package com.sepideh.lilo.password.domain

import kotlinx.serialization.Serializable

@Serializable
data class Password(
    val id: String = "", val title: String = "", val username: String = "",
    val secret: String = "", val website: String = "", val notes: String = "",
    val createdAt: Long = 0, val updatedAt: Long = 0,
    val serviceId: String? = null, val serviceName: String = "",
    val serviceNameFa: String = serviceName,
) {
    val hasIdentifier: Boolean get() = title.isNotBlank() || username.isNotBlank()
    val displayTitle: String get() = title.ifBlank { serviceName }
    override fun toString(): String = "Password(redacted)"
}

enum class UnlockResult { SUCCESS, CANCELLED, UNAVAILABLE, KEY_UNAVAILABLE, FAILED }

data class SealedPassword(val format: Int, val nonce: ByteArray, val ciphertext: ByteArray, val wrappedKey: ByteArray = byteArrayOf())

class VaultLockedException : IllegalStateException("Passwords are locked")

class VaultKeyUnavailableException : IllegalStateException("Password key unavailable")

data class PasswordUnlockRequest(val id: String, val sealed: SealedPassword)
class DeviceLockRequiredException : IllegalStateException("Device screen lock required")
