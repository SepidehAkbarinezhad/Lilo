package com.sepideh.lilo.password.domain

import kotlinx.coroutines.flow.Flow

data class PasswordService(val id: String, val name: String, val isDefault: Boolean = false)
interface PasswordServiceRepository {
    fun observe(): Flow<List<PasswordService>>
    suspend fun create(name: String): PasswordService
    suspend fun rename(id: String, name: String)
    suspend fun usage(id: String): Int
    suspend fun delete(id: String, replacementId: String?)
}

/** HTTP(S) website input, including Unicode domain labels. Empty input is optional. */
fun normalizePasswordWebsite(input: String): String? {
    val raw = input.trim()
    if (raw.isEmpty()) return ""
    if (raw.any { it.isWhitespace() || it.isISOControl() } || '\\' in raw) return null
    val url = if ("://" in raw) raw else "https://$raw"
    val scheme = url.substringBefore("://").lowercase()
    if (scheme != "https" && scheme != "http") return null
    val rest = url.substringAfter("://")
    val authority = rest.takeWhile { it != '/' && it != '?' && it != '#' }
    if ('@' in authority) return null
    val host = authority.substringBefore(':').replace('。', '.').replace('．', '.').replace('｡', '.')
    if (':' in authority) {
        val port = authority.substringAfter(':')
        if (port.any { !it.isDigit() } || port.toIntOrNull() !in 1..65535) return null
    }
    val labels = host.split('.')
    if (host.length > 253 || labels.size < 2 || labels.any { label ->
        label.isEmpty() || label.length > 63 || label.first() == '-' || label.last() == '-' ||
            label.any { !it.isLetterOrDigit() && it != '-' }
    }) return null
    if (labels.all { label -> label.all { it in '0'..'9' } }) {
        if (labels.size != 4 || labels.any { it.toIntOrNull() !in 0..255 }) return null
    } else if (labels.last().all { it.isDigit() }) return null
    val suffix = rest.removePrefix(authority)
    if (Regex("%(?![0-9a-fA-F]{2})").containsMatchIn(suffix)) return null
    return "$scheme://$host${authority.removePrefix(authority.substringBefore(':'))}$suffix"
}
