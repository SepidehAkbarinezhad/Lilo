package com.sepideh.lilo.password.data

import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.validation.PasswordValidationError
import com.sepideh.lilo.password.domain.validation.PasswordValidationException
import com.sepideh.lilo.password.domain.validation.PasswordValidator
import com.sepideh.lilo.password.domain.model.matching
import com.sepideh.lilo.password.domain.model.PasswordService
import com.sepideh.lilo.password.domain.repository.PasswordServiceRepository
import com.sepideh.lilo.password.domain.crypto.SealedPassword
import com.sepideh.lilo.password.domain.crypto.VaultLockedException

import com.sepideh.lilo.password.data.local.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.test.*

class PasswordRepositoryTest {
    private val key = ByteArray(64) { it.toByte() }

    @Test fun invalidInputsReturnTypedErrorsBeforeAnyPersistenceOrSealing() = runBlocking<Unit> {
        val dao = MemoryDao()
        val crypto = TestSession()
        val repo = PasswordRepositoryImpl(dao, crypto, TestServices())
        val valid = Password(title = "Mail", serviceId = "google", secret = "secret")
        val invalid = listOf(
            valid.copy(title = " ", username = "\t"),
            valid.copy(title = "a".repeat(61)),
            valid.copy(serviceId = null),
            valid.copy(serviceId = " \t"),
            valid.copy(secret = ""),
            valid.copy(secret = " \t\r\n"),
            valid.copy(secret = "\u00a0\u2003"),
            valid.copy(secret = "x".repeat(257)),
            valid.copy(website = "bad address"),
            Password(),
        )
        for (password in invalid) {
            val failure = assertFailsWith<PasswordValidationException> { repo.save(password) }
            assertEquals(PasswordValidator.validate(password), failure.errors)
            assertTrue(failure.errors.isNotEmpty())
            assertNull(crypto.secretInput)
            assertTrue(dao.rows.isEmpty())
        }
    }

    @Test fun validBoundariesPreserveSecretAndExistingNormalization() = runBlocking<Unit> {
        val dao = MemoryDao()
        val repo = PasswordRepositoryImpl(dao, TestSession(), TestServices())
        val secret = " " + "ر".repeat(254) + " "
        val saved = repo.save(Password(title = "  " + "ع".repeat(60) + "  ", username = " user ",
            serviceId = "google", secret = secret, website = " HTTP://example.com:8080/path "))
        assertEquals(secret, saved.secret)
        assertEquals("ع".repeat(60), saved.title)
        assertEquals("user", saved.username)
        assertEquals("http://example.com:8080/path", saved.website)
        assertEquals(saved.title, dao.rows.getValue(saved.id).title)
    }

    @Test fun technicalFailureIsNotConvertedToValidationFailure() = runBlocking<Unit> {
        val dao = MemoryDao()
        val expected = IllegalStateException("Storage unavailable")
        dao.beforeSave = { throw expected }
        val repo = PasswordRepositoryImpl(dao, TestSession(), TestServices())
        val failure = assertFailsWith<IllegalStateException> {
            repo.save(Password(title = "Mail", serviceId = "google", secret = " valid "))
        }
        assertSame(expected, failure)
        assertTrue(dao.rows.isEmpty())
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


    @Test fun payloadIsExactSecretUtf8AndNotesArePlainRoomMetadata() = runBlocking<Unit> {
        val dao = MemoryDao()
        val crypto = TestSession()
        val repo = PasswordRepositoryImpl(dao, crypto, TestServices())
        for (secret in listOf(" \tvalue\n", "  رمز 🔑 e\u0301\u0000\r\n ", "{\"secret\":\"not a JSON payload\"}")) {
            val saved = repo.save(Password(title = "Metadata", username = "public-user", website = "example.org",
                serviceId = "google", secret = secret, notes = "  private یادداشت\n"))
            val envelope = repo.unlockRequest(saved.id).sealed
            assertContentEquals(secret.encodeToByteArray(), decryptRepositoryFixture(key, envelope, passwordAad(saved.id)))
            assertEquals(saved.notes, dao.rows.getValue(saved.id).notes)
            assertTrue(crypto.secretInput!!.all { it == 0.toByte() })

            assertFalse(crypto.unlocked)
            crypto.unlock(key.copyOf(), saved.id)
            assertEquals(saved.copy(serviceName = "Google", serviceNameFa = "گوگل"), repo.get(saved.id))
            crypto.lock()
        }
    }

    @Test fun roomMetadataIsAuthoritativeAndServiceChangesNeedNoDecryption() = runBlocking<Unit> {
        val dao = MemoryDao()
        val services = TestServices()
        val crypto = TestSession()
        val repo = PasswordRepositoryImpl(dao, crypto, services)
        val saved = repo.save(Password(title = "Old", username = "old-user", serviceId = "google", secret = "secret", notes = "private"))
        val row = dao.rows.getValue(saved.id)
        // Models metadata-only writes, including the service DAO's reassignment path.
        dao.rows[saved.id] = row.copy(title = "Current", username = "current-user", website = "https://new.example",
            serviceId = "github", createdAt = 10, updatedAt = 20, notes = "Updated plain note")
        services.rows.value = services.rows.value.map { if (it.id == "github") it.copy(name = "Renamed", nameFa = "جدید") else it }
        val browsable = repo.observePasswords().first()
        assertEquals(saved.id, browsable.matching("current-user", false).single().id)
        assertEquals(saved.id, browsable.matching("new.example", false).single().id)
        assertEquals(saved.id, browsable.matching("جدید", false).single().id)
        assertTrue(browsable.matching("private", false).isEmpty())
        assertTrue(browsable.matching("secret", false).isEmpty())
        assertEquals("", browsable.single().notes)
        assertFalse(crypto.unlocked)
        assertContentEquals(row.ciphertext, dao.rows.getValue(saved.id).ciphertext)
        crypto.unlock(key.copyOf(), saved.id)
        assertEquals(browsable.single().copy(secret = saved.secret, notes = "Updated plain note"), repo.get(saved.id))
    }

    @Test fun updateReplacesSecretAndCanClearPlainNotes() = runBlocking<Unit> {
        val dao = MemoryDao()
        val crypto = TestSession()
        val repo = PasswordRepositoryImpl(dao, crypto, TestServices())
        val original = repo.save(Password(title = "Mail", serviceId = "google", secret = "first", notes = "private"))
        crypto.unlock(key.copyOf(), original.id)
        val saved = repo.save(original.copy(title = "New", username = "user", website = "example.org", serviceId = "github", secret = " \tsecond\n ", notes = ""))
        val loaded = assertNotNull(repo.get(saved.id))
        assertEquals(saved.copy(serviceName = "GitHub", serviceNameFa = "گیت‌هاب"), loaded)
        assertEquals(original.createdAt, loaded.createdAt)
        assertEquals(1, dao.rows.size)
        assertEquals("", dao.rows.getValue(saved.id).notes)
        repo.delete(saved.id)
        assertNull(repo.get(saved.id))
        assertTrue(repo.observePasswords().first().isEmpty())
    }

    @Test fun rejectsCorruptSecretCiphertextAndWrongFormatOrIdentity() = runBlocking<Unit> {
        val dao = MemoryDao()
        val crypto = TestSession()
        val repo = PasswordRepositoryImpl(dao, crypto, TestServices())
        val saved = repo.save(Password(title = "Mail", serviceId = "google", secret = "secret", notes = "note"))
        crypto.unlock(key.copyOf())
        val row = dao.rows.getValue(saved.id)
        fun damaged(bytes: ByteArray) = bytes.copyOf().apply { this[lastIndex] = (this[lastIndex].toInt() xor 1).toByte() }
        for (bad in listOf(
            row.copy(ciphertext = damaged(row.ciphertext)),
            row.copy(format = -1),
        )) {
            dao.rows[saved.id] = bad
            assertFails { repo.get(saved.id) }
        }
        dao.rows["wrong-id"] = row.copy(id = "wrong-id")
        assertFails { repo.get("wrong-id") }
    }

    @Test fun failedSaveOrSessionChangeDoesNotReplaceStoredRecordAndWipesInputs() = runBlocking<Unit> {
        val dao = MemoryDao()
        val crypto = TestSession()
        val repo = PasswordRepositoryImpl(dao, crypto, TestServices())
        val saved = repo.save(Password(title = "Mail", serviceId = "google", secret = "original", notes = "original note"))
        val row = dao.rows.getValue(saved.id)
        crypto.unlock(key.copyOf(), saved.id)
        crypto.beforeSeal = { error("Encryption failed") }
        assertFails { repo.save(saved.copy(secret = "replacement", notes = "new note")) }
        assertEquals(row, dao.rows[saved.id])
        assertTrue(crypto.secretInput!!.all { it == 0.toByte() })

        crypto.beforeSeal = { crypto.lock() }
        assertFailsWith<VaultLockedException> { repo.save(saved.copy(secret = "replacement")) }
        assertEquals(row, dao.rows[saved.id])
    }

    @Test fun invalidUtf8IsRejectedInsteadOfSilentlyChangingTheSecret() = runBlocking<Unit> {
        val dao = MemoryDao()
        val crypto = TestSession()
        val repo = PasswordRepositoryImpl(dao, crypto, TestServices())
        val saved = repo.save(Password(title = "Mail", serviceId = "google", secret = "original"))
        val invalid = encryptRepositoryFixture(key, byteArrayOf(0xc3.toByte(), 0x28), saved.id)
        val row = dao.rows.getValue(saved.id)
        dao.rows[saved.id] = row.copy(nonce = invalid.nonce, ciphertext = invalid.ciphertext)
        crypto.unlock(key.copyOf(), saved.id)
        assertFails { repo.get(saved.id) }
        assertFails { repo.save(saved.copy(secret = "\uD800")) }
        assertTrue(crypto.secretInput!!.all { it == 0.toByte() })
    }
    private class TestSession : SessionVaultCrypto() {
        var beforeSeal: () -> Unit = {}
        var secretInput: ByteArray? = null
        override suspend fun seal(plaintext: ByteArray, recordId: String): SealedPassword {
            secretInput = plaintext
            beforeSeal()
            return encryptRepositoryFixture(ByteArray(64) { it.toByte() }, plaintext, recordId)
        }
        fun unlock(key: ByteArray, id: String? = null) = acceptAuthenticatedKey(key, id)
        override suspend fun decrypt(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray {
            return decryptRepositoryFixture(key, record, aad)
        }
    }
    private class TestServices : PasswordServiceRepository {
        val rows = MutableStateFlow(listOf(PasswordService("google", "Google", true, "گوگل"), PasswordService("github", "GitHub", true, "گیت‌هاب")))
        override fun observe() = rows
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
        val missingService = assertFailsWith<IllegalArgumentException> { repo.save(Password(title = "Mail", serviceId = "deleted", secret = "secret")) }
        assertFalse(missingService is PasswordValidationException)
        val invalidWebsite = assertFailsWith<PasswordValidationException> { repo.save(Password(title = "Mail", serviceId = "google", secret = "secret", website = "bad address")) }
        assertEquals(setOf(PasswordValidationError.InvalidWebsite), invalidWebsite.errors)
        assertEquals(1, dao.rows.size)
    }
    private class MemoryDao : PasswordDao {
        val rows = linkedMapOf<String, PasswordEntity>()
        var beforeRead: () -> Unit = {}
        var beforeSave: () -> Unit = {}
        override fun observe(): Flow<List<PasswordMetadata>> = flowOf(rows.values.map {
            PasswordMetadata(it.id, it.title, it.username, it.website, it.createdAt, it.updatedAt, it.serviceId)
        })
        override suspend fun get(id: String): PasswordEntity? { beforeRead(); return rows[id] }
        override suspend fun serviceExists(id: String) = if (id in listOf("google", "github")) 1 else 0
        override suspend fun save(record: PasswordEntity) { beforeSave(); rows[record.id] = record }
        override suspend fun delete(id: String) { rows.remove(id) }
    }
}

// Platform actuals use real AES-GCM or CBC/HMAC; key wrapping and OS prompts have separate tests.
internal expect suspend fun encryptRepositoryFixture(key: ByteArray, secret: ByteArray, id: String): SealedPassword
internal expect suspend fun decryptRepositoryFixture(key: ByteArray, record: SealedPassword, aad: ByteArray): ByteArray
