package com.sepideh.lilo.password.presentation

import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.clipboard.PasswordClipboard
import com.sepideh.lilo.password.domain.repository.PasswordRepository
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest
import com.sepideh.lilo.password.domain.crypto.SealedPassword
import com.sepideh.lilo.password.domain.crypto.UnlockResult
import com.sepideh.lilo.password.domain.crypto.VaultCrypto
import com.sepideh.lilo.password.domain.crypto.VaultSession

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.password.presentation.detail.PasswordDetailAction
import com.sepideh.lilo.password.presentation.detail.PasswordDetailViewModel
import com.sepideh.lilo.password.presentation.list.PasswordListAction
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

    @Test fun oversizedFieldsDoNotSaveAndValidBoundaryCanBeRetried() = runTest {
        val repository = FakeRepository()
        val model = tracked(PasswordDetailViewModel(repository, FakeCrypto()))
        model.onAction(PasswordDetailAction.OnInitialize(null))
        model.onAction(PasswordDetailAction.OnServiceSelected("google"))
        model.onAction(PasswordDetailAction.OnTitleChanged("a".repeat(61)))
        model.onAction(PasswordDetailAction.OnSecretChanged("valid"))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertEquals(0, repository.writes)
        model.onAction(PasswordDetailAction.OnTitleChanged("a".repeat(60)))
        model.onAction(PasswordDetailAction.OnSecretChanged("x".repeat(257)))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertEquals(0, repository.writes)
        assertEquals(257, model.state.value.record.secret.length)
        model.onAction(PasswordDetailAction.OnSecretChanged(" " + "x".repeat(254) + " "))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertEquals(1, repository.writes)
        assertEquals(" " + "x".repeat(254) + " ", model.state.value.record.secret)
    }

    @Test fun addOpensAndSavesWithoutUnlocking() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val model = tracked(PasswordDetailViewModel(repository, crypto))
        model.onAction(PasswordDetailAction.OnInitialize(null))
        runCurrent()
        assertFalse(model.state.value.locked)
        assertTrue(model.state.value.visible)
        model.onAction(PasswordDetailAction.OnServiceSelected("google"))
        model.onAction(PasswordDetailAction.OnTitleChanged("Mail"))
        model.onAction(PasswordDetailAction.OnSecretChanged(" private "))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertEquals(1, repository.writes)
        assertEquals(0, repository.reads)
        assertFalse(crypto.unlocked)
    }

    @Test fun listBrowsesWhileLockedAndRevealRequiresAuthenticationEachTime() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val model = tracked(PasswordListViewModel(repository, crypto, FakeClipboard()))
        runCurrent()
        assertEquals("Mail", model.state.value.records.single().title)
        assertEquals("", model.state.value.records.single().secret)
        model.onAction(PasswordListAction.OnReveal("one"))
        runCurrent()
        assertNotNull(model.state.value.request)
        assertEquals(0, repository.reads)
        crypto.unlock("one")
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
        runCurrent()
        assertEquals("private", model.state.value.revealedSecret)
        model.onAction(PasswordListAction.OnReveal("one")) // Eye hides immediately.
        runCurrent()
        assertEquals("", model.state.value.revealedSecret)
        assertFalse(crypto.unlocked)
        model.onAction(PasswordListAction.OnReveal("one"))
        runCurrent()
        assertNotNull(model.state.value.request)
        assertEquals(1, repository.reads) // A second read still awaits a new authentication.
    }

    @Test fun cancellingAuthenticationLeavesListUsableAndNothingRevealed() = runTest {
        val repository = FakeRepository()
        val model = tracked(PasswordListViewModel(repository, FakeCrypto(), FakeClipboard()))
        runCurrent()
        model.onAction(PasswordListAction.OnReveal("one"))
        runCurrent()
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.CANCELLED))
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
        val model = tracked(PasswordListViewModel(repository, crypto, FakeClipboard()))
        runCurrent()
        model.onAction(PasswordListAction.OnReveal("one"))
        runCurrent()
        crypto.unlock("one")
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
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
        model.onAction(PasswordDetailAction.OnInitialize("one"))
        runCurrent()
        assertTrue(model.state.value.locked)
        assertEquals(0, repository.reads)
        crypto.unlock("one")
        runCurrent()
        assertTrue(model.state.value.locked) // A new session cannot reopen a dismissed editor.
        model.onAction(PasswordDetailAction.OnInitialize("one"))
        runCurrent()
        assertFalse(model.state.value.locked)
        assertEquals("private", model.state.value.record.secret)
    }

    @Test fun failedSavePreservesDraftForRetry() = runTest {
        val repository = FakeRepository().apply { failSave = true }
        val model = tracked(PasswordDetailViewModel(repository, FakeCrypto()))
        model.onAction(PasswordDetailAction.OnInitialize(null))
        runCurrent()
        model.onAction(PasswordDetailAction.OnServiceSelected("google"))
        model.onAction(PasswordDetailAction.OnTitleChanged("Mail"))
        model.onAction(PasswordDetailAction.OnSecretChanged(" keep spaces "))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertTrue(model.state.value.error)
        assertEquals(" keep spaces ", model.state.value.record.secret)
        repository.failSave = false
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertEquals(2, repository.writes)
        assertFalse(model.state.value.error)
    }

    @Test fun backgroundClearsUnsavedSecretWithoutBlockingAddForm() = runTest {
        val crypto = FakeCrypto()
        val model = tracked(PasswordDetailViewModel(FakeRepository(), crypto))
        model.onAction(PasswordDetailAction.OnInitialize(null))
        runCurrent()
        model.onAction(PasswordDetailAction.OnServiceSelected("google"))
        model.onAction(PasswordDetailAction.OnTitleChanged("Mail"))
        model.onAction(PasswordDetailAction.OnSecretChanged("private"))
        model.onAction(PasswordDetailAction.OnNotesChanged("sensitive"))
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
        model.onAction(PasswordDetailAction.OnInitialize(null))
        model.onAction(PasswordDetailAction.OnSecretChanged("secret"))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertTrue(model.state.value.serviceInvalid)
        assertEquals(0, repo.writes)
        model.onAction(PasswordDetailAction.OnServiceSelected("google"))
        model.onAction(PasswordDetailAction.OnWebsiteChanged("bad address"))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertTrue(model.state.value.websiteInvalid)
        assertEquals(0, repo.writes)
        model.onAction(PasswordDetailAction.OnWebsiteChanged("example.com"))
        model.onAction(PasswordDetailAction.OnUsernameChanged("hello@example.com"))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertEquals(1, repo.writes)
    }
    @Test fun emptyIdentifierCannotBeSavedButEitherFieldIsEnough() = runTest {
        val repo = FakeRepository()
        val model = tracked(PasswordDetailViewModel(repo, FakeCrypto()))
        model.onAction(PasswordDetailAction.OnInitialize(null))
        model.onAction(PasswordDetailAction.OnServiceSelected("google"))
        model.onAction(PasswordDetailAction.OnSecretChanged("secret"))
        model.onAction(PasswordDetailAction.OnTitleChanged(" "))
        model.onAction(PasswordDetailAction.OnUsernameChanged(" "))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertTrue(model.state.value.identifierInvalid)
        assertEquals(0, repo.writes)
        model.onAction(PasswordDetailAction.OnTitleChanged("Personal"))
        model.onAction(PasswordDetailAction.OnSave)
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
        model.onAction(PasswordDetailAction.OnInitialize("one"))
        runCurrent()
        assertFalse(model.state.value.locked)
        crypto.lock()
        runCurrent()
        assertEquals(listOf(true), exits)
        assertEquals("", model.state.value.record.secret)
        assertNull(model.state.value.request)
    }

    @Test fun unauthenticatedDetailReturnsWithoutAnUnlockScreenOrReadingSecrets() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val model = tracked(PasswordDetailViewModel(repository, crypto))
        val exits = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            model.baseOneTimeEvents.collect { exits += it.navigateBack }
        }
        model.onAction(PasswordDetailAction.OnInitialize("one"))
        runCurrent()
        assertEquals(listOf(true), exits)
        assertTrue(model.state.value.locked)
        assertNull(model.state.value.request)
        assertFalse(model.state.value.loading)
        assertEquals(0, repository.reads)
        model.onAction(PasswordDetailAction.OnSecretChanged("must not be saved"))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertEquals(0, repository.writes)
        crypto.unlock("one")
        runCurrent()
        assertTrue(model.state.value.locked)
        assertEquals("", model.state.value.record.secret)
        assertEquals(0, repository.reads)
    }

    @Test fun unavailableDirectPasswordRemainsLockedAndCannotSave() = runTest {
        val repository = FakeRepository()
        val model = tracked(PasswordDetailViewModel(repository, FakeCrypto()))
        model.onAction(PasswordDetailAction.OnInitialize("deleted"))
        runCurrent()
        assertTrue(model.state.value.loadFailed)
        assertTrue(model.state.value.locked)
        model.onAction(PasswordDetailAction.OnTitleChanged("Replacement"))
        model.onAction(PasswordDetailAction.OnServiceSelected("google"))
        model.onAction(PasswordDetailAction.OnSecretChanged("secret"))
        model.onAction(PasswordDetailAction.OnSave)
        runCurrent()
        assertEquals(0, repository.reads)
        assertEquals(0, repository.writes)
        assertEquals("", model.state.value.record.secret)
    }

    @Test fun hiddenCopyAuthenticatesWithoutRevealingAndShowsTemporaryConfirmation() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val clipboard = FakeClipboard()
        val model = tracked(PasswordListViewModel(repository, crypto, clipboard))
        runCurrent()
        model.onAction(PasswordListAction.OnCopy("one"))
        runCurrent()
        assertNotNull(model.state.value.request)
        assertTrue(clipboard.values.isEmpty())
        assertEquals(0, repository.reads)
        crypto.unlock("one")
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
        runCurrent()
        assertEquals(listOf("private"), clipboard.values)
        assertNull(model.state.value.revealedId)
        assertEquals("", model.state.value.revealedSecret)
        assertFalse(crypto.unlocked)
        assertEquals("one", model.state.value.copiedId)
        advanceTimeBy(2_001)
        runCurrent()
        assertNull(model.state.value.copiedId)
    }

    @Test fun visiblePasswordCopiesExactValueWithoutAnotherAuthenticationOrRead() = runTest {
        val repository = FakeRepository().apply { records.value = records.value.map { it.copy(secret = " private ") } }
        val crypto = FakeCrypto()
        val clipboard = FakeClipboard()
        val model = tracked(PasswordListViewModel(repository, crypto, clipboard))
        runCurrent()
        model.onAction(PasswordListAction.OnReveal("one"))
        runCurrent()
        crypto.unlock("one")
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
        runCurrent()
        val session = crypto.session.value
        model.onAction(PasswordListAction.OnCopy("one"))
        runCurrent()
        assertEquals(listOf(" private "), clipboard.values)
        assertNull(model.state.value.request)
        assertEquals(1, repository.reads)
        assertEquals(session, crypto.session.value)
        assertEquals(" private ", model.state.value.revealedSecret)
        model.onAction(PasswordListAction.OnHide)
        model.onAction(PasswordListAction.OnCopy("one"))
        runCurrent()
        assertNotNull(model.state.value.request)
        assertEquals(1, clipboard.values.size)
    }

    @Test fun failedCancelledAndWrongRecordAuthenticationNeverCopy() = runTest {
        for (result in listOf(UnlockResult.CANCELLED, UnlockResult.FAILED, UnlockResult.SUCCESS)) {
            val crypto = FakeCrypto()
            val clipboard = FakeClipboard()
            val model = tracked(PasswordListViewModel(FakeRepository(), crypto, clipboard))
            runCurrent()
            model.onAction(PasswordListAction.OnCopy("one"))
            runCurrent()
            if (result == UnlockResult.SUCCESS) crypto.unlock("another")
            model.onAction(PasswordListAction.OnAuthenticated(result))
            runCurrent()
            assertTrue(clipboard.values.isEmpty())
            assertNull(model.state.value.busyId)
            assertEquals("", model.state.value.revealedSecret)
            assertFalse(crypto.unlocked)
        }
    }

    @Test fun visibleCopyCannotReuseAnExpiredOrReplacedSessionEvenBeforeCollectorRuns() = runTest {
        for (replaceWithUnlockedSession in listOf(false, true)) {
            val crypto = FakeCrypto()
            val clipboard = FakeClipboard()
            val model = tracked(PasswordListViewModel(FakeRepository(), crypto, clipboard))
            runCurrent()
            model.onAction(PasswordListAction.OnReveal("one"))
            runCurrent()
            crypto.unlock("one")
            model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
            runCurrent()
            if (replaceWithUnlockedSession) crypto.unlock("one") else crypto.lock()
            model.onAction(PasswordListAction.OnCopy("one")) // Do not give the session collector time to clear the visible value first.
            runCurrent()
            assertTrue(clipboard.values.isEmpty())
            assertNotNull(model.state.value.request)
            assertEquals("", model.state.value.revealedSecret)
        }
    }

    @Test fun lockingWhilePasswordReadIsInFlightPreventsClipboardWrite() = runTest {
        val release = CompletableDeferred<Unit>()
        val repository = FakeRepository().apply { beforeRead = { release.await() } }
        val crypto = FakeCrypto()
        val clipboard = FakeClipboard()
        val model = tracked(PasswordListViewModel(repository, crypto, clipboard))
        runCurrent()
        model.onAction(PasswordListAction.OnCopy("one"))
        runCurrent()
        crypto.unlock("one")
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
        runCurrent()
        crypto.lock()
        release.complete(Unit)
        runCurrent()
        assertTrue(clipboard.values.isEmpty())
        assertNull(model.state.value.busyId)
        assertEquals("", model.state.value.revealedSecret)
    }

    @Test fun deletedPasswordAndLeavingScreenCannotCopy() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val clipboard = FakeClipboard()
        val model = tracked(PasswordListViewModel(repository, crypto, clipboard))
        runCurrent()
        model.onAction(PasswordListAction.OnCopy("deleted"))
        runCurrent()
        assertTrue(model.state.value.error)
        assertTrue(clipboard.values.isEmpty())
        model.onAction(PasswordListAction.OnCopy("one"))
        runCurrent()
        model.onResetState()
        crypto.unlock("one")
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
        runCurrent()
        assertTrue(clipboard.values.isEmpty())
        assertNull(model.state.value.request)
    }

    @Test fun clipboardFailureDoesNotClaimSuccessOrRevealHiddenSecret() = runTest {
        val crypto = FakeCrypto()
        val clipboard = FakeClipboard().apply { fail = true }
        val model = tracked(PasswordListViewModel(FakeRepository(), crypto, clipboard))
        runCurrent()
        model.onAction(PasswordListAction.OnCopy("one"))
        runCurrent()
        crypto.unlock("one")
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
        runCurrent()
        assertTrue(model.state.value.error)
        assertNull(model.state.value.copiedId)
        assertEquals("", model.state.value.revealedSecret)
        assertFalse(crypto.unlocked)
    }

    @Test fun navigatingToAuthenticatedEditorStillRetainsItsRecordSession() = runTest {
        val crypto = FakeCrypto()
        val model = tracked(PasswordListViewModel(FakeRepository(), crypto, FakeClipboard()))
        runCurrent()
        model.onAction(PasswordListAction.OnEdit("one"))
        runCurrent()
        crypto.unlock("one")
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
        runCurrent()
        model.onResetState()
        assertTrue(crypto.unlocked)
        assertEquals("one", crypto.session.value.recordId)
        assertEquals("", model.state.value.revealedSecret)
    }

    @Test fun filtersKeepDraftsSeparateAndRemovedServicesAreReconciled() = runTest {
        val model = tracked(PasswordListViewModel(FakeRepository(), FakeCrypto(), FakeClipboard()))
        model.onAction(PasswordListAction.OnSearchChanged(true))
        model.onAction(PasswordListAction.OnQueryChanged("Mail"))
        model.onAction(PasswordListAction.OnSearchChanged(false))
        assertEquals("", model.state.value.query)
        model.onAction(PasswordListAction.OnSortOpen(true))
        model.onAction(PasswordListAction.OnServiceSelected("google"))
        model.onAction(PasswordListAction.OnSortChanged(true))
        assertNull(model.state.value.serviceId)
        assertFalse(model.state.value.alphabetical)
        model.onAction(PasswordListAction.OnApplySort)
        assertEquals("google", model.state.value.serviceId)
        assertTrue(model.state.value.alphabetical)
        assertFalse(model.state.value.sortOpen)
        model.onAction(PasswordListAction.OnSortOpen(true))
        model.onAction(PasswordListAction.OnResetFilters)
        assertEquals("google", model.state.value.serviceId)
        model.onAction(PasswordListAction.OnSortOpen(false))
        assertEquals("google", model.state.value.draftServiceId)
        model.onAction(PasswordListAction.OnServicesChanged(emptySet()))
        assertNull(model.state.value.serviceId)
        assertNull(model.state.value.draftServiceId)
        model.onAction(PasswordListAction.OnResetFilters)
        model.onAction(PasswordListAction.OnApplySort)
        assertFalse(model.state.value.alphabetical)
    }

    @Test fun discardActionsPreserveDraftUntilConfirmedAndThenClearSensitiveFields() = runTest {
        val model = tracked(PasswordDetailViewModel(FakeRepository(), FakeCrypto()))
        val exits = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            model.baseOneTimeEvents.collect { exits += it.navigateBack }
        }
        model.onAction(PasswordDetailAction.OnInitialize(null))
        model.onAction(PasswordDetailAction.OnSecretChanged("private"))
        model.onAction(PasswordDetailAction.OnNotesChanged("sensitive"))
        model.onAction(PasswordDetailAction.OnVisibility)
        assertFalse(model.state.value.visible)
        model.onAction(PasswordDetailAction.Back)
        assertTrue(model.state.value.discardOpen)
        model.onAction(PasswordDetailAction.KeepEditing)
        assertFalse(model.state.value.discardOpen)
        assertEquals("private", model.state.value.record.secret)
        runCurrent()
        assertTrue(exits.isEmpty())
        model.onAction(PasswordDetailAction.Back)
        model.onAction(PasswordDetailAction.Discard)
        runCurrent()
        assertEquals(listOf(true), exits)
        assertEquals("", model.state.value.record.secret)
        assertEquals("", model.state.value.record.notes)
    }

    @Test fun deletingRevealedPasswordClearsItsSessionAndMetadata() = runTest {
        val repository = FakeRepository()
        val crypto = FakeCrypto()
        val model = tracked(PasswordListViewModel(repository, crypto, FakeClipboard()))
        runCurrent()
        model.onAction(PasswordListAction.OnReveal("one"))
        runCurrent()
        crypto.unlock("one")
        model.onAction(PasswordListAction.OnAuthenticated(UnlockResult.SUCCESS))
        runCurrent()
        model.onAction(PasswordListAction.OnDeleteSelected("one"))
        model.onAction(PasswordListAction.OnDeleteSelected(null))
        assertEquals(1, model.state.value.records.size)
        model.onAction(PasswordListAction.OnDeleteSelected("one"))
        model.onAction(PasswordListAction.OnDeleteConfirm)
        runCurrent()
        assertNull(model.state.value.deleteId)
        assertTrue(model.state.value.records.isEmpty())
        assertEquals("", model.state.value.revealedSecret)
        assertFalse(crypto.unlocked)
    }

    private class FakeClipboard : PasswordClipboard {
        val values = mutableListOf<String>()
        var fail = false
        override fun copyPassword(value: String) { check(!fail); values += value }
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
        var beforeRead: suspend () -> Unit = {}
        override fun observePasswords(): Flow<List<Password>> = records.map { rows -> rows.map { it.copy(secret = "", notes = "") } }
        override suspend fun unlockRequest(id: String): PasswordUnlockRequest {
            require(records.value.any { it.id == id })
            return PasswordUnlockRequest(id, SealedPassword(PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP, byteArrayOf(), byteArrayOf()))
        }
        override suspend fun get(id: String): Password? { reads++; beforeRead(); return records.value.find { it.id == id } }
        override suspend fun save(password: Password): Password {
            writes++
            if (failSave) error("Storage unavailable")
            return password.copy(id = password.id.ifEmpty { "saved" })
        }
        override suspend fun delete(id: String) { records.update { list -> list.filterNot { it.id == id } } }
    }
}
