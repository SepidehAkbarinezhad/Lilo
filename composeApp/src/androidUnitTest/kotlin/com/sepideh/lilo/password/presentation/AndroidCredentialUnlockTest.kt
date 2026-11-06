package com.sepideh.lilo.password.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.sepideh.lilo.password.domain.crypto.PasswordEncryptionFormat
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest
import com.sepideh.lilo.password.domain.crypto.SealedPassword
import com.sepideh.lilo.password.domain.crypto.UnlockResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AndroidCredentialUnlockTest {
    @BeforeTest
    fun setUp() {
        // The callback and lifecycle events run on Android's main thread, where
        // Main.immediate does not dispatch. runTest still queues child coroutines.
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun confirmedCredentialBeforeForegroundWaitsForResumeAndDeliversOnlyOnce() = runTest {
        val owner = TestOwner(Lifecycle.State.CREATED)
        val unlocked = mutableListOf<PasswordUnlockRequest>()
        val results = mutableListOf<UnlockResult>()
        val delivery = launch {
            deliverCredentialUnlockResult(owner.lifecycle, true, request, {
                assertEquals(Lifecycle.State.RESUMED, owner.lifecycle.currentState)
                unlocked += it
                UnlockResult.SUCCESS
            }, results::add)
        }
        runCurrent()
        assertTrue(unlocked.isEmpty())
        assertTrue(results.isEmpty())
        assertFalse(delivery.isCompleted)

        owner.lifecycle.currentState = Lifecycle.State.STARTED
        runCurrent()
        assertTrue(unlocked.isEmpty())
        assertTrue(results.isEmpty())

        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        runCurrent()
        assertEquals(listOf(request), unlocked)
        assertEquals(listOf(UnlockResult.SUCCESS), results)
        assertTrue(delivery.isCompleted)

        owner.lifecycle.currentState = Lifecycle.State.CREATED
        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        runCurrent()
        assertEquals(listOf(request), unlocked)
        assertEquals(listOf(UnlockResult.SUCCESS), results)
    }

    @Test
    fun confirmedCredentialWhileStartedStillWaitsForResume() = runTest {
        val owner = TestOwner(Lifecycle.State.STARTED)
        var unlocks = 0
        val results = mutableListOf<UnlockResult>()
        launch {
            deliverCredentialUnlockResult(owner.lifecycle, true, request, {
                unlocks++
                UnlockResult.SUCCESS
            }, results::add)
        }
        runCurrent()
        assertEquals(0, unlocks)
        assertTrue(results.isEmpty())

        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        runCurrent()
        assertEquals(1, unlocks)
        assertEquals(listOf(UnlockResult.SUCCESS), results)
    }

    @Test
    fun confirmedCredentialWhileResumedUnlocksWithoutAnotherLifecycleEvent() = runTest {
        val owner = TestOwner(Lifecycle.State.RESUMED)
        val unlocked = mutableListOf<PasswordUnlockRequest>()
        val results = mutableListOf<UnlockResult>()
        deliverCredentialUnlockResult(owner.lifecycle, true, request, {
            unlocked += it
            UnlockResult.SUCCESS
        }, results::add)
        assertEquals(listOf(request), unlocked)
        assertEquals(listOf(UnlockResult.SUCCESS), results)
    }

    @Test
    fun rejectedCredentialOrMissingRequestCancelsWithoutWaitingOrUnlocking() = runTest {
        for ((confirmed, pendingRequest) in listOf(false to request, true to null)) {
            val owner = TestOwner(Lifecycle.State.CREATED)
            val results = mutableListOf<UnlockResult>()
            deliverCredentialUnlockResult(owner.lifecycle, confirmed, pendingRequest, {
                error("Rejected or missing credentials must not unlock")
            }, results::add)
            assertEquals(listOf(UnlockResult.CANCELLED), results)
        }
    }

    @Test
    fun destroyedScreenCancelsPendingDeliveryWithoutUnlocking() = runTest {
        val owner = TestOwner(Lifecycle.State.CREATED)
        val results = mutableListOf<UnlockResult>()
        val delivery = launch {
            deliverCredentialUnlockResult(owner.lifecycle, true, request, {
                error("A destroyed screen must not unlock")
            }, results::add)
        }
        runCurrent()
        owner.lifecycle.currentState = Lifecycle.State.DESTROYED
        runCurrent()
        assertTrue(delivery.isCancelled)
        assertTrue(results.isEmpty())
    }

    @Test
    fun alreadyDestroyedScreenDoesNotUnlock() = runTest {
        val owner = TestOwner(Lifecycle.State.CREATED)
        owner.lifecycle.currentState = Lifecycle.State.DESTROYED
        val results = mutableListOf<UnlockResult>()
        val delivery = launch {
            deliverCredentialUnlockResult(owner.lifecycle, true, request, {
                error("A destroyed screen must not unlock")
            }, results::add)
        }
        runCurrent()
        assertTrue(delivery.isCancelled)
        assertTrue(results.isEmpty())
    }

    @Test
    fun disposedCoroutineCannotUnlockWhenScreenLaterResumes() = runTest {
        val owner = TestOwner(Lifecycle.State.CREATED)
        val results = mutableListOf<UnlockResult>()
        val delivery = launch {
            deliverCredentialUnlockResult(owner.lifecycle, true, request, {
                error("A disposed unlocker must not unlock")
            }, results::add)
        }
        runCurrent()
        delivery.cancel()
        runCurrent()
        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        runCurrent()
        assertTrue(delivery.isCancelled)
        assertTrue(results.isEmpty())
    }

    @Test
    fun unlockFailureIsDeliveredAfterResume() = runTest {
        val owner = TestOwner(Lifecycle.State.CREATED)
        val results = mutableListOf<UnlockResult>()
        launch {
            deliverCredentialUnlockResult(owner.lifecycle, true, request, {
                UnlockResult.KEY_UNAVAILABLE
            }, results::add)
        }
        runCurrent()
        assertTrue(results.isEmpty())
        owner.lifecycle.currentState = Lifecycle.State.RESUMED
        runCurrent()
        assertEquals(listOf(UnlockResult.KEY_UNAVAILABLE), results)
    }

    private class TestOwner(initialState: Lifecycle.State) : LifecycleOwner {
        override val lifecycle = LifecycleRegistry.createUnsafe(this).apply {
            currentState = initialState
        }
    }

    private val request = PasswordUnlockRequest(
        id = "record",
        sealed = SealedPassword(
            format = PasswordEncryptionFormat.ANDROID_AES_GCM_RSA_OAEP,
            nonce = ByteArray(12),
            ciphertext = ByteArray(16),
            wrappedKey = ByteArray(256),
        ),
    )
}
