package com.sepideh.lilo.password.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.password.domain.*
import com.sepideh.lilo.password.presentation.detail.PasswordDetailViewModel
import com.sepideh.lilo.password.presentation.list.PasswordListViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class PasswordViewModelTest {
    private val stores = mutableListOf<ViewModelStore>()
    @BeforeTest fun setUp() { Dispatchers.setMain(StandardTestDispatcher()) }
    @AfterTest fun tearDown() { stores.forEach { it.clear() }; Dispatchers.resetMain() }
    private fun <T : ViewModel> tracked(model: T): T {
        stores += ViewModelStore().apply { put("test", model) }; return model
    }

    @Test fun addOpensAndSavesWithoutUnlocking() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val model = tracked(PasswordDetailViewModel(repository, crypto))
        model.initialize(null)
        runCurrent()
        assertFalse(model.state.value.locked)
        assertTrue(model.state.value.visible)
        model.edit { it.copy(serviceId = "google", title = "Mail", secret = " private ") }
        model.save()
        runCurrent()
        assertEquals(1, repository.writes)
        assertEquals(0, repository.reads)
        assertFalse(crypto.unlocked)
    }

    @Test fun listBrowsesWhileLockedAndRevealRequiresAuthenticationEachTime() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val model = tracked(PasswordListViewModel(repository, crypto))
        runCurrent()
        assertEquals("Mail", model.state.value.records.single().title)
        assertEquals("", model.state.value.records.single().secret)
        model.open("one")
        runCurrent()
        assertNotNull(model.state.value.request)
        assertEquals(0, repository.reads)
        crypto.unlock("one")
        model.authenticated(UnlockResult.SUCCESS)
        runCurrent()
        assertEquals("private", model.state.value.revealedSecret)
        model.open("one") // Eye hides immediately.
        runCurrent()
        assertEquals("", model.state.value.revealedSecret)
        assertFalse(crypto.unlocked)
        model.open("one")
        runCurrent()
        assertNotNull(model.state.value.request)
        assertEquals(1, repository.reads) // A second read still awaits a new authentication.
    }

    @Test fun cancellingAuthenticationLeavesListUsableAndNothingRevealed() = runTest {
        val repository = FakeRepository()
        val model = tracked(PasswordListViewModel(repository, FakeCrypto()))
        runCurrent()
        model.open("one")
        runCurrent()
        model.authenticated(UnlockResult.CANCELLED)
        runCurrent()
        assertNull(model.state.value.busyId)
        assertNull(model.state.value.request)
        assertNull(model.state.value.revealedId)
        assertEquals(1, model.state.value.records.size)
        assertEquals(0, repository.reads)
    }

    @Test fun backgroundHidesRevealedPasswordButKeepsListMetadata() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val model = tracked(PasswordListViewModel(repository, crypto))
        runCurrent()
        model.open("one")
        runCurrent()
        crypto.unlock("one")
        model.authenticated(UnlockResult.SUCCESS)
        runCurrent()
        crypto.lock()
        runCurrent()
        assertEquals("", model.state.value.revealedSecret)
        assertEquals("Mail", model.state.value.records.single().title)
    }

    @Test fun editorRequiresItsOwnRecordAuthentication() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto().apply { unlock("another") }
        val model = tracked(PasswordDetailViewModel(repository, crypto))
        model.initialize("one")
        runCurrent()
        assertTrue(model.state.value.locked)
        assertEquals(0, repository.reads)
        crypto.unlock("one")
        runCurrent()
        assertFalse(model.state.value.locked)
        assertEquals("private", model.state.value.record.secret)
    }

    @Test fun failedSavePreservesDraftForRetry() = runTest {
        val repository = FakeRepository().apply { failSave = true }
        val model = tracked(PasswordDetailViewModel(repository, FakeCrypto()))
        model.initialize(null)
        runCurrent()
        model.edit { it.copy(serviceId = "google", title = "Mail", secret = " keep spaces ") }
        model.save()
        runCurrent()
        assertTrue(model.state.value.error)
        assertEquals(" keep spaces ", model.state.value.record.secret)
        repository.failSave = false
        model.save()
        runCurrent()
        assertEquals(2, repository.writes)
        assertFalse(model.state.value.error)
    }

    @Test fun backgroundClearsUnsavedSecretWithoutBlockingAddForm() = runTest {
        val crypto = FakeCrypto()
        val model = tracked(PasswordDetailViewModel(FakeRepository(), crypto))
        model.initialize(null)
        runCurrent()
        model.edit { it.copy(serviceId = "google", title = "Mail", secret = "private", notes = "sensitive") }
        crypto.lock()
        runCurrent()
        assertFalse(model.state.value.locked)
        assertEquals("Mail", model.state.value.record.title)
        assertEquals("", model.state.value.record.secret)
        assertEquals("", model.state.value.record.notes)
    }

    @Test fun serviceIsRequiredAndTitleIsOptional() = runTest {
        val repo = FakeRepository()
        val model = tracked(PasswordDetailViewModel(repo, FakeCrypto()))
        model.initialize(null)
        model.edit { it.copy(secret = "secret") }
        model.save()
        runCurrent()
        assertTrue(model.state.value.serviceInvalid)
        assertEquals(0, repo.writes)
        model.edit { it.copy(serviceId = "google", website = "bad address") }
        model.save()
        runCurrent()
        assertTrue(model.state.value.websiteInvalid)
        assertEquals(0, repo.writes)
        model.edit { it.copy(website = "example.com", username = "hello@example.com") }
        model.save()
        runCurrent()
        assertEquals(1, repo.writes)
    }
    @Test fun emptyIdentifierCannotBeSavedButEitherFieldIsEnough() = runTest {
        val repo = FakeRepository()
        val model = tracked(PasswordDetailViewModel(repo, FakeCrypto()))
        model.initialize(null)
        model.edit { it.copy(serviceId = "google", secret = "secret", title = " ", username = " ") }
        model.save()
        runCurrent()
        assertTrue(model.state.value.identifierInvalid)
        assertEquals(0, repo.writes)
        model.edit { it.copy(title = "Personal") }
        model.save()
        runCurrent()
        assertEquals(1, repo.writes)
    }

    @Test fun losingEditorAuthenticationReturnsToListWithoutAnUnlockScreen() = runTest {
        val crypto = FakeCrypto().apply { unlock("one") }
        val model = tracked(PasswordDetailViewModel(FakeRepository(), crypto))
        val exits = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            model.baseOneTimeEvents.collect { exits += it.navigateBack }
        }
        model.initialize("one")
        runCurrent()
        assertFalse(model.state.value.locked)
        crypto.lock()
        runCurrent()
        assertEquals(listOf(true), exits)
        assertEquals("", model.state.value.record.secret)
        assertNull(model.state.value.request)
    }

    @Test fun directDetailSurvivesAuthenticationStartAndCanRetryAfterCancellation() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val model = tracked(PasswordDetailViewModel(repository, crypto))
        val exits = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            model.baseOneTimeEvents.collect { exits += it.navigateBack }
        }
        model.initialize("one")
        runCurrent()
        repeat(2) {
            crypto.lock() // Platform unlockers invalidate the previous session before prompting.
            runCurrent()
            assertTrue(model.state.value.locked)
            assertEquals("one", model.state.value.request?.id)
            assertFalse(model.state.value.loading)
            assertEquals(0, repository.reads)
            assertTrue(exits.isEmpty())
        }
        crypto.unlock("one")
        runCurrent()
        assertFalse(model.state.value.locked)
        assertEquals("one", model.state.value.record.id)
        assertEquals("private", model.state.value.record.secret)
        assertEquals(1, repository.reads)
    }

    @Test fun unavailableDirectPasswordRemainsLockedAndCannotSave() = runTest {
        val repository = FakeRepository()
        val model = tracked(PasswordDetailViewModel(repository, FakeCrypto()))
        model.initialize("deleted")
        runCurrent()
        assertTrue(model.state.value.loadFailed)
        assertTrue(model.state.value.locked)
        model.edit { it.copy(title = "Replacement", serviceId = "google", secret = "secret") }
        model.save()
        runCurrent()
        assertEquals(0, repository.reads)
        assertEquals(0, repository.writes)
        assertEquals("", model.state.value.record.secret)
    }

    private class FakeCrypto : VaultCrypto {
        override val session = MutableStateFlow(VaultSession())
        fun unlock(id: String) { session.value = VaultSession(generation + 1, true, id) }
        override fun lock() { session.value = VaultSession(generation + 1, false) }
        override suspend fun seal(plaintext: ByteArray, recordId: String): SealedPassword = error("Not used")
        override suspend fun open(record: SealedPassword, recordId: String): ByteArray = error("Not used")
    }
    private class FakeRepository : PasswordRepository {
        val records = MutableStateFlow(listOf(Password(id = "one", title = "Mail", secret = "private")))
        var reads = 0
        var writes = 0
        var failSave = false
        override fun observePasswords(): Flow<List<Password>> = records.map { rows -> rows.map { it.copy(secret = "", notes = "") } }
        override suspend fun unlockRequest(id: String): PasswordUnlockRequest {
            require(records.value.any { it.id == id })
            return PasswordUnlockRequest(id, SealedPassword(3, byteArrayOf(), byteArrayOf()))
        }
        override suspend fun get(id: String): Password? { reads++; return records.value.find { it.id == id } }
        override suspend fun save(password: Password): Password {
            writes++
            if (failSave) error("Storage unavailable")
            return password.copy(id = password.id.ifEmpty { "saved" })
        }
        override suspend fun delete(id: String) { records.update { list -> list.filterNot { it.id == id } } }
    }
}
