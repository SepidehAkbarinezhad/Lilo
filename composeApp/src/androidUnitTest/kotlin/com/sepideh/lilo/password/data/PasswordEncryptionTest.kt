package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.model.PasswordService
import com.sepideh.lilo.password.domain.repository.PasswordServiceRepository
import com.sepideh.lilo.password.domain.crypto.SealedPassword
import com.sepideh.lilo.password.domain.crypto.VaultLockedException

import com.sepideh.lilo.password.data.local.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.test.*

class PasswordEncryptionTest {
    private val key = ByteArray(32) { it.toByte() }
    private val wrappedKey = ByteArray(256) // Cipher-only tests; RSA round-trip is tested separately.
    private val aad = "lilo-password-v1:record".encodeToByteArray()
    @Test fun roundTripAndFreshNonce() {
        val input = "secret فارسی".encodeToByteArray()
        val first = AndroidPasswordCipher.encrypt(key, input, aad, wrappedKey)
        val second = AndroidPasswordCipher.encrypt(key, input, aad, wrappedKey)
        assertEquals(PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP, first.format)
        assertContentEquals(input, AndroidPasswordCipher.decrypt(key, first, aad))
        assertFalse(first.nonce.contentEquals(second.nonce))
        assertFalse(first.ciphertext.contentEquals(second.ciphertext))
    }
    @Test fun rejectsTamperingAndWrongIdentityOrKey() {
        val encrypted = AndroidPasswordCipher.encrypt(key, "private".encodeToByteArray(), aad, wrappedKey)
        for (index in listOf(0, encrypted.ciphertext.lastIndex)) {
            val damaged = encrypted.ciphertext.copyOf().apply { this[index] = (this[index].toInt() xor 1).toByte() }
            assertFails { AndroidPasswordCipher.decrypt(key, encrypted.copy(ciphertext = damaged), aad) }
        }
        val nonce = encrypted.nonce.copyOf().apply { this[0] = (this[0].toInt() xor 1).toByte() }
        assertFails { AndroidPasswordCipher.decrypt(key, encrypted.copy(nonce = nonce), aad) }
        assertFails { AndroidPasswordCipher.decrypt(key, encrypted, "other-row".encodeToByteArray()) }
        assertFails { AndroidPasswordCipher.decrypt(ByteArray(32), encrypted, aad) }
        for (format in listOf(PasswordEncryptionFormat.IOS_AES_CBC_HMAC_RSA_OAEP, 3, 4, 0, -1, Int.MAX_VALUE)) {
            assertFailsWith<IllegalArgumentException> { AndroidPasswordCipher.decrypt(key, encrypted.copy(format = format), aad) }
        }
        for (malformed in listOf(
            encrypted.copy(wrappedKey = byteArrayOf()), // Obsolete whole-vault envelope, despite reused ID.
            encrypted.copy(wrappedKey = ByteArray(255)), encrypted.copy(wrappedKey = ByteArray(257)),
            encrypted.copy(nonce = ByteArray(11)), encrypted.copy(nonce = ByteArray(13)),
            encrypted.copy(ciphertext = ByteArray(15)),
        )) {
            assertFailsWith<IllegalArgumentException> { AndroidPasswordCipher.validateRecord(malformed) }
            assertFailsWith<IllegalArgumentException> { AndroidPasswordCipher.decrypt(key, malformed, aad) }
        }
        assertFailsWith<IllegalArgumentException> { AndroidPasswordCipher.decrypt(ByteArray(16), encrypted, aad) }
    }
    @Test fun repositoryExposesMetadataButProtectsSecretsAndPreservesCreationDate() = runBlocking<Unit> {
        val dao = MemoryDao()
        val crypto = TestSession().apply { unlock(key.copyOf()) }
        val repo = PasswordRepositoryImpl(dao, crypto, TestServices())
        val initial = repo.save(Password(serviceId = "google", title = "Private bank", username = "private-user", secret = " private-secret ", website = "example.org", notes = "private-notes"))
        assertEquals(initial.copy(serviceName = "Google", serviceNameFa = "گوگل"), repo.get(initial.id))
        val row = dao.rows.getValue(initial.id)
        for (plain in listOf(initial.title, initial.username, initial.secret, initial.website, initial.notes)) {
            assertFalse(row.ciphertext.decodeToString().contains(plain))
        }
        val edited = repo.save(initial.copy(title = "Updated"))
        assertEquals(initial.id, edited.id)
        assertEquals(initial.createdAt, edited.createdAt)
        assertEquals(" private-secret ", edited.secret)
        assertEquals(1, dao.rows.size)
        assertFailsWith<IllegalArgumentException> { repo.save(Password(title = "", secret = "x")) }
        assertFailsWith<IllegalArgumentException> { repo.save(Password(id = "missing", title = "x", secret = "x")) }
        crypto.lock()
        assertFailsWith<VaultLockedException> { repo.get(initial.id) }
        assertEquals(edited.title, repo.observePasswords().first().single().title)
        assertEquals("گوگل", repo.observePasswords().first().single().serviceNameFa)
        assertEquals("", repo.observePasswords().first().single().secret)
        assertEquals("", repo.observePasswords().first().single().notes)
        assertFailsWith<VaultLockedException> { repo.save(initial) }
        assertEquals(1, dao.rows.size)
        repo.delete(initial.id)
        assertTrue(dao.rows.isEmpty())
    }
    @Test fun lockWhileDatabaseReadIsSuspendedRejectsPlaintext() = runBlocking<Unit> {
        val dao = MemoryDao()
        val crypto = TestSession().apply { unlock(key.copyOf()) }
        val repo = PasswordRepositoryImpl(dao, crypto, TestServices())
        val record = repo.save(Password(serviceId = "google", title = "name", secret = "secret"))
        dao.beforeRead = { crypto.lock() }
        assertFailsWith<VaultLockedException> { repo.get(record.id) }
    }
    @Test fun newRecordCanBeSavedWhileLockedButCannotBeRead() = runBlocking<Unit> {
        val crypto = TestSession()
        val repo = PasswordRepositoryImpl(MemoryDao(), crypto, TestServices())
        val record = repo.save(Password(serviceId = "google", title = "Mail", secret = "private"))
        assertFalse(crypto.unlocked)
        assertEquals("Mail", repo.observePasswords().first().single().title)
        assertFailsWith<VaultLockedException> { repo.get(record.id) }
        crypto.unlock(key.copyOf())
        assertEquals("private", repo.get(record.id)?.secret)
    }

    @Test fun publicKeySaveUsesIndependentKeysAndBindsCiphertextToRecord() {
        val pair = java.security.KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        val first = AndroidPasswordCipher.seal(pair.public, "first secret".encodeToByteArray(), "one")
        val second = AndroidPasswordCipher.seal(pair.public, "second secret".encodeToByteArray(), "two")
        fun unwrap(record: SealedPassword): ByteArray = javax.crypto.Cipher.getInstance("RSA/ECB/OAEPPadding").run {
            init(javax.crypto.Cipher.DECRYPT_MODE, pair.private, AndroidPasswordCipher.oaep)
            doFinal(record.wrappedKey)
        }
        val firstKey = unwrap(first)
        val secondKey = unwrap(second)
        try {
            assertEquals(PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP, first.format)
            assertEquals(256, first.wrappedKey.size)
            assertFalse(firstKey.contentEquals(secondKey))
            assertFails { unwrap(first.copy(wrappedKey = first.wrappedKey.copyOf().apply { this[0] = (this[0].toInt() xor 1).toByte() })) }
            assertFails { AndroidPasswordCipher.seal(pair.public, byteArrayOf(1), " ") }
            assertEquals("first secret", AndroidPasswordCipher.decrypt(firstKey, first, "lilo-password-v1:one".encodeToByteArray()).decodeToString())
            assertFails { AndroidPasswordCipher.decrypt(firstKey, second, "lilo-password-v1:two".encodeToByteArray()) }
            assertFails { AndroidPasswordCipher.decrypt(firstKey, first, "lilo-password-v1:two".encodeToByteArray()) }
        } finally { firstKey.fill(0); secondKey.fill(0) }
    }

    private class TestSession : SessionVaultCrypto() {
        override suspend fun seal(plaintext: ByteArray, recordId: String) =
            AndroidPasswordCipher.encrypt(ByteArray(32) { it.toByte() }, plaintext, "lilo-password-v1:$recordId".encodeToByteArray(), ByteArray(256))
        fun unlock(key: ByteArray) = acceptAuthenticatedKey(key)
        override suspend fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray) = AndroidPasswordCipher.decrypt(key, record, aad)
    }
    private class TestServices : PasswordServiceRepository {
        override fun observe() = flowOf(listOf(PasswordService("google", "Google", true, "گوگل")))
        override suspend fun create(name: String): PasswordService = error("unused")
        override suspend fun rename(id: String, name: String) = Unit
        override suspend fun usage(id: String) = 0
        override suspend fun delete(id: String, replacementId: String?) = Unit
    }
    @Test fun titleIsOptionalButUnknownServiceAndInvalidWebsiteAreRejected() = runBlocking<Unit> {
        val dao = MemoryDao()
        val repo = PasswordRepositoryImpl(dao, TestSession(), TestServices())
        val saved = repo.save(Password(serviceId = "google", username = "user", secret = "secret", website = "مثال.ir/راهنما"))
        assertEquals("", saved.title)
        assertEquals("https://مثال.ir/راهنما", saved.website)
        assertEquals("Google", repo.observePasswords().first().single().displayTitle)
        assertFailsWith<IllegalArgumentException> { repo.save(Password(serviceId = "deleted", secret = "secret")) }
        assertFailsWith<IllegalArgumentException> { repo.save(Password(serviceId = "google", secret = "secret", website = "bad address")) }
        assertEquals(1, dao.rows.size)
    }
    private class MemoryDao : PasswordDao {
        val rows = linkedMapOf<String, PasswordEntity>()
        var beforeRead: () -> Unit = {}
        override fun observe(): Flow<List<PasswordMetadata>> = flowOf(rows.values.map {
            PasswordMetadata(it.id, it.title, it.username, it.website, it.createdAt, it.updatedAt, it.serviceId)
        })
        override suspend fun get(id: String): PasswordEntity? { beforeRead(); return rows[id] }
        override suspend fun serviceExists(id: String) = if (id == "google") 1 else 0
        override suspend fun save(record: PasswordEntity) { rows[record.id] = record }
        override suspend fun delete(id: String) { rows.remove(id) }
    }
}
