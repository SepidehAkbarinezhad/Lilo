package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.data.local.*
import com.sepideh.lilo.password.domain.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PasswordRepositoryImpl(private val dao: PasswordDao, private val crypto: VaultCrypto, private val services: PasswordServiceRepository) : PasswordRepository {
    // Browsing returns only the explicitly public metadata, never decrypted fields.
    override fun observePasswords() = combine(dao.observe(), services.observe()) { rows, services -> rows.map { row ->
        Password(id = row.id, title = row.title, username = row.username, website = row.website,
            createdAt = row.createdAt, updatedAt = row.updatedAt, serviceId = row.serviceId, serviceName = services.find { it.id == row.serviceId }?.name.orEmpty(),
            serviceNameFa = services.find { it.id == row.serviceId }?.nameFa.orEmpty())
    } }

    override suspend fun unlockRequest(id: String): PasswordUnlockRequest {
        val row = requireNotNull(dao.get(id))
        return PasswordUnlockRequest(id, row.sealed())
    }

    override suspend fun get(id: String): Password? {
        val session = crypto.session.value
        if (!session.unlocked || (session.recordId != null && session.recordId != id)) throw VaultLockedException()
        val row = dao.get(id) ?: return null
        val bytes = crypto.open(row.sealed(), id)
        val result = try { Json.decodeFromString<Password>(bytes.decodeToString()).also { require(it.id == id) } }
        finally { bytes.fill(0) }
        if (crypto.session.value != session) throw VaultLockedException()
        // Preserve the old encrypted record while making its metadata browsable after first authorized access.
        if (row.createdAt == 0L) dao.updateMetadata(id, result.title, result.username, result.website, result.createdAt, result.updatedAt)
        if (crypto.session.value != session) throw VaultLockedException()
        val service = services.observe().first().find { it.id == row.serviceId }
        if (crypto.session.value != session) throw VaultLockedException()
        return result.copy(serviceId = row.serviceId, serviceName = service?.name.orEmpty(), serviceNameFa = service?.nameFa.orEmpty())
    }

    override suspend fun save(password: Password): Password {
        requirePasswordFieldLengths(password)
        require(password.hasIdentifier && !password.serviceId.isNullOrBlank() && password.secret.isNotEmpty())
        val website = requireNotNull(normalizePasswordWebsite(password.website))
        val session = crypto.session.value
        val previous = password.id.takeIf { it.isNotEmpty() }?.let { id ->
            if (!session.unlocked || (session.recordId != null && session.recordId != id)) throw VaultLockedException()
            requireNotNull(dao.get(id))
        }
        val now = Clock.System.now().toEpochMilliseconds()
        val saved = password.copy(id = previous?.id ?: Uuid.random().toString(), title = password.title.trim(),
            username = password.username.trim(), website = website, serviceName = "", serviceNameFa = "",
            createdAt = previous?.createdAt?.takeIf { it != 0L } ?: password.createdAt.takeIf { it != 0L } ?: now, updatedAt = now)
        val bytes = Json.encodeToString(saved).encodeToByteArray()
        val sealed = try { crypto.seal(bytes, saved.id) } finally { bytes.fill(0) }
        if (crypto.session.value != session) throw VaultLockedException()
        dao.saveWithService(PasswordEntity(saved.id, sealed.format, sealed.nonce, sealed.ciphertext, sealed.wrappedKey,
            saved.title, saved.username, saved.website, saved.createdAt, saved.updatedAt, saved.serviceId))
        return saved
    }

    override suspend fun delete(id: String) { dao.delete(id) }
    private fun PasswordEntity.sealed() = SealedPassword(format, nonce, ciphertext, wrappedKey)
}
