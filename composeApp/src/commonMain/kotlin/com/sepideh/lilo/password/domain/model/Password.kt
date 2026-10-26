package com.sepideh.lilo.password.domain.model

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
