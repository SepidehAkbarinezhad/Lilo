package com.sepideh.lilo.home.presentation

import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.repository.PasswordRepository
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest
import com.sepideh.lilo.password.domain.crypto.SealedPassword
import com.sepideh.lilo.password.domain.crypto.UnlockResult
import com.sepideh.lilo.password.domain.crypto.VaultCrypto
import com.sepideh.lilo.password.domain.crypto.VaultSession

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.app.navigation.AppRoutes
import kotlinx.coroutines.launch
import com.sepideh.lilo.home.domain.*
import com.sepideh.lilo.home.presentation.model.*
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.reminder.*
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class HomeFailureTest {
    @Test fun failedReportCanRetryWithoutDuplicatingHealthySubscriptions() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            var attempts = 0
            val card = object : FeatureCard<ReportDetail> {
                override val feature = LiloFeature.NOTES
                override fun getReportDetailStrategy() = object : ReportDetailStrategy<ReportDetail> {
                    override fun observeReportDetail(): Flow<ReportDetail> = flow {
                        attempts++
                        if (attempts == 1) error("Read failed")
                        emit(NoteReportDetail("Recovered", "", 1, null, 0))
                        kotlinx.coroutines.awaitCancellation()
                    }
                }
                override fun getReportRender() = object : ReportRenderStrategy<ReportDetail> {
                    @Composable override fun Render(detail: ReportDetail) = Unit
                }
            }
            val model = HomeViewModel(FeatureCardFactory(listOf(card)), TaskMutations(UnusedTasks(), UnusedScheduler()), FakePasswords(), FakeCrypto())
            store.put("home", model)
            model.onAction(HomeAction.ObserveFeature(LiloFeature.NOTES))
            runCurrent()
            assertEquals(setOf(LiloFeature.NOTES), model.state.value.failedFeatures)
            model.onAction(HomeAction.RetryFeature(LiloFeature.NOTES))
            runCurrent()
            assertTrue(model.state.value.failedFeatures.isEmpty())
            assertEquals("Recovered", (model.state.value.reportDetails[LiloFeature.NOTES] as NoteReportDetail).latestTitle)
            model.onAction(HomeAction.ObserveFeature(LiloFeature.NOTES))
            model.onAction(HomeAction.RetryFeature(LiloFeature.NOTES))
            runCurrent()
            assertEquals(2, attempts)
        } finally { store.clear(); Dispatchers.resetMain() }
    }
    @Test fun homeAuthenticatesExactPasswordBeforeNavigatingWithoutDecrypting() = runTest {
        withPasswordModel { model, repository, crypto, routes ->
            model.onAction(HomeAction.OpenPassword("one"))
            model.onAction(HomeAction.OpenPassword("another"))
            runCurrent()
            assertEquals(1, repository.requests)
            assertEquals("one", model.state.value.passwordRequest?.id)
            assertTrue(routes.isEmpty())
            crypto.unlock("one")
            model.passwordAuthenticated(UnlockResult.SUCCESS)
            runCurrent()
            assertEquals(listOf<AppRoutes?>(AppRoutes.Passwords.Detail("one")), routes)
            assertNull(model.state.value.passwordRequest)
            model.onResetState()
            assertTrue(crypto.unlocked) // The destination still needs this authenticated key.
        }
    }

    @Test fun cancelledHomeAuthenticationStaysOnHomeAndCanRetry() = runTest {
        withPasswordModel { model, _, crypto, routes ->
            model.onAction(HomeAction.OpenPassword("one"))
            runCurrent()
            model.passwordAuthenticated(UnlockResult.CANCELLED)
            runCurrent()
            assertTrue(routes.isEmpty())
            assertNull(model.state.value.openingPasswordId)
            assertNull(model.state.value.passwordAuthError)
            assertFalse(crypto.unlocked)
            model.onAction(HomeAction.OpenPassword("one"))
            runCurrent()
            assertNotNull(model.state.value.passwordRequest)
        }
    }

    @Test fun wrongRecordSessionCannotOpenPasswordFromHome() = runTest {
        withPasswordModel { model, _, crypto, routes ->
            model.onAction(HomeAction.OpenPassword("one"))
            runCurrent()
            crypto.unlock("another")
            model.passwordAuthenticated(UnlockResult.SUCCESS)
            runCurrent()
            assertTrue(routes.isEmpty())
            assertEquals(UnlockResult.FAILED, model.state.value.passwordAuthError)
            assertFalse(crypto.unlocked)
        }
    }

    @Test fun deletedPasswordNeverStartsAuthenticationAndResetIgnoresLateResult() = runTest {
        withPasswordModel { model, _, crypto, routes ->
            model.onAction(HomeAction.OpenPassword("deleted"))
            runCurrent()
            assertTrue(model.state.value.passwordOpenFailed)
            assertNull(model.state.value.passwordRequest)
            assertNull(model.state.value.openingPasswordId)
            model.onAction(HomeAction.OpenPassword("one"))
            runCurrent()
            model.onResetState()
            model.passwordAuthenticated(UnlockResult.SUCCESS)
            runCurrent()
            assertTrue(routes.isEmpty())
            assertFalse(crypto.unlocked)
        }
    }

    private suspend fun TestScope.withPasswordModel(block: suspend (HomeViewModel, FakePasswords, FakeCrypto, MutableList<AppRoutes?>) -> Unit) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val store = ViewModelStore()
        try {
            val repository = FakePasswords()
            val crypto = FakeCrypto()
            val model = HomeViewModel(FeatureCardFactory(emptyList()), TaskMutations(UnusedTasks(), UnusedScheduler()), repository, crypto)
            store.put("home", model)
            val routes = mutableListOf<AppRoutes?>()
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.baseOneTimeEvents.collect { routes += it.route } }
            block(model, repository, crypto, routes)
        } finally { store.clear(); Dispatchers.resetMain() }
    }
    private class FakePasswords : PasswordRepository {
        var requests = 0
        override fun observePasswords(): Flow<List<Password>> = error("Not used")
        override suspend fun unlockRequest(id: String): PasswordUnlockRequest {
            requests++
            require(id == "one")
            return PasswordUnlockRequest(id, SealedPassword(PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP, byteArrayOf(), byteArrayOf()))
        }
        override suspend fun get(id: String): Password? = error("Home must not decrypt credentials")
        override suspend fun save(password: Password): Password = error("Not used")
        override suspend fun delete(id: String): Unit = error("Not used")
    }
    private class FakeCrypto : VaultCrypto {
        override val session = MutableStateFlow(VaultSession())
        override fun lock() { session.value = VaultSession(generation + 1) }
        fun unlock(id: String) { session.value = VaultSession(generation + 1, true, id) }
        override suspend fun seal(plaintext: ByteArray, recordId: String): SealedPassword = error("Not used")
        override suspend fun open(record: SealedPassword, recordId: String): ByteArray = error("Not used")
    }
    private class UnusedTasks : TaskRepository {
        override fun getAllTasks(): Flow<List<Task>> = error("Not used")
        override fun getTasksByFilter(done: Boolean?, priority: List<Int>): Flow<List<Task>> = error("Not used")
        override suspend fun clearGroup(id: Long): Unit = error("Not used")
        override suspend fun deleteTask(id: Long): Unit = error("Not used")
        override suspend fun upsertTask(task: Task): Long = error("Not used")
        override suspend fun getTaskById(id: Long): Task? = error("Not used")
    }
    private class UnusedScheduler : ReminderScheduler {
        override suspend fun scheduleReminder(reminder: Reminder): Unit = error("Not used")
        override suspend fun cancelReminder(taskId: Long): Unit = error("Not used")
    }
}
