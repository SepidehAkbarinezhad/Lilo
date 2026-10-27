package com.sepideh.lilo.password.data

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import platform.Foundation.NSNotificationCenter
import platform.UIKit.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class IosPasswordForegroundTest {
    @Test fun successfulAuthenticationWaitsForAppToBecomeActive() = runTest {
        val center = NSNotificationCenter()
        var state = UIApplicationState.UIApplicationStateInactive
        val result = async { awaitPasswordForeground(CompletableDeferred(), center) { state } }
        runCurrent()
        assertFalse(result.isCompleted)
        state = UIApplicationState.UIApplicationStateActive
        center.postNotificationName(UIApplicationDidBecomeActiveNotification, null)
        runCurrent()
        assertTrue(result.await())
    }

    @Test fun alreadyActiveDoesNotWaitForAnotherNotification() = runTest {
        assertTrue(awaitPasswordForeground(CompletableDeferred(), NSNotificationCenter()) {
            UIApplicationState.UIApplicationStateActive
        })
    }

    @Test fun alreadyBackgroundCannotAcceptAuthentication() = runTest {
        assertFalse(awaitPasswordForeground(CompletableDeferred(), NSNotificationCenter()) {
            UIApplicationState.UIApplicationStateBackground
        })
    }

    @Test fun backgroundWhileWaitingCannotBeUndoneByLaterActivation() = runTest {
        val center = NSNotificationCenter()
        var state = UIApplicationState.UIApplicationStateInactive
        val result = async { awaitPasswordForeground(CompletableDeferred(), center) { state } }
        runCurrent()
        state = UIApplicationState.UIApplicationStateBackground
        center.postNotificationName(UIApplicationDidEnterBackgroundNotification, null)
        state = UIApplicationState.UIApplicationStateActive
        center.postNotificationName(UIApplicationDidBecomeActiveNotification, null)
        runCurrent()
        assertFalse(result.await())
    }

    @Test fun cancelledAttemptCannotBeReactivated() = runTest {
        val center = NSNotificationCenter()
        val activation = CompletableDeferred<Boolean>()
        var state = UIApplicationState.UIApplicationStateInactive
        val result = async { awaitPasswordForeground(activation, center) { state } }
        runCurrent()
        activation.complete(false) // cancelUnlock() on dismissal or a superseding attempt.
        state = UIApplicationState.UIApplicationStateActive
        center.postNotificationName(UIApplicationDidBecomeActiveNotification, null)
        runCurrent()
        assertFalse(result.await())
    }

    @Test fun cancellationBeforeWaitingIsPreservedEvenIfAlreadyActive() = runTest {
        val activation = CompletableDeferred<Boolean>().apply { complete(false) }
        assertFalse(awaitPasswordForeground(activation, NSNotificationCenter()) {
            UIApplicationState.UIApplicationStateActive
        })
    }

    @Test fun coroutineCancellationStopsWaiting() = runTest {
        val center = NSNotificationCenter()
        val result = async { awaitPasswordForeground(CompletableDeferred(), center) {
            UIApplicationState.UIApplicationStateInactive
        } }
        runCurrent()
        result.cancel()
        runCurrent()
        center.postNotificationName(UIApplicationDidBecomeActiveNotification, null)
        assertTrue(result.isCancelled)
    }

    @Test fun appMustStillBeActiveWhenWaitResumes() = runTest {
        val center = NSNotificationCenter()
        var state = UIApplicationState.UIApplicationStateInactive
        val result = async { awaitPasswordForeground(CompletableDeferred(), center) { state } }
        runCurrent()
        state = UIApplicationState.UIApplicationStateActive
        center.postNotificationName(UIApplicationDidBecomeActiveNotification, null)
        state = UIApplicationState.UIApplicationStateBackground
        runCurrent()
        assertFalse(result.await())
    }
}
