package com.sepideh.lilo.password.data

import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PasswordClipboardExpiryTest {
    private class Clipboard {
        var clip: PasswordClipExpiry? = PasswordClipExpiry("first", 100_000, 100_000)
        var accessible = true
        var fails = false
        var clears = 0
        fun read(): PasswordClipExpiry? {
            if (fails) error("restricted")
            return clip.takeIf { accessible }
        }
        fun clear(token: String) {
            if (fails) error("restricted")
            if (accessible && clip?.token == token) { clip = null; clears++ }
        }
    }
    private fun TestScope.controller(clipboard: Clipboard) = PasswordClipboardExpiry(
        backgroundScope, clipboard::read, clipboard::clear,
        { 100_000 + testScheduler.currentTime }, { 100_000 + testScheduler.currentTime },
    )

    @Test fun clearsAtDeadlineAndNotBefore() = runTest {
        val clipboard = Clipboard()
        controller(clipboard).refresh()
        advanceTimeBy(59_999); runCurrent()
        assertEquals(0, clipboard.clears)
        advanceTimeBy(1); runCurrent()
        assertEquals(1, clipboard.clears)
    }

    @Test fun inaccessibleDeadlineIsRetriedOnWindowFocus() = runTest {
        val clipboard = Clipboard()
        val expiry = controller(clipboard)
        expiry.onFocusChanged(true); runCurrent()
        expiry.onFocusChanged(false); clipboard.accessible = false
        advanceTimeBy(60_000); runCurrent()
        assertEquals(0, clipboard.clears)
        clipboard.accessible = true
        expiry.onFocusChanged(true); runCurrent()
        assertEquals(1, clipboard.clears)
    }

    @Test fun processRecreationRecoversExpiredMetadataWithoutPasswordOrPreferences() = runTest {
        val clipboard = Clipboard()
        advanceTimeBy(90_000)
        controller(clipboard).onFocusChanged(true); runCurrent()
        assertEquals(1, clipboard.clears)
    }

    @Test fun processRecreationPreservesOriginalDeadline() = runTest {
        val clipboard = Clipboard()
        advanceTimeBy(30_000)
        controller(clipboard).onFocusChanged(true); runCurrent()
        advanceTimeBy(29_999); runCurrent()
        assertEquals(0, clipboard.clears)
        advanceTimeBy(1); runCurrent()
        assertEquals(1, clipboard.clears)
    }

    @Test fun replacementContentIsNeverCleared() = runTest {
        val clipboard = Clipboard()
        controller(clipboard).refresh(); runCurrent()
        clipboard.clip = null // A clip without Lilo ownership metadata.
        advanceTimeBy(60_000); runCurrent()
        assertEquals(0, clipboard.clears)
    }

    @Test fun secondCopyGetsItsOwnLifetime() = runTest {
        val clipboard = Clipboard()
        val expiry = controller(clipboard)
        expiry.refresh(); runCurrent()
        advanceTimeBy(30_000)
        clipboard.clip = PasswordClipExpiry("second", 130_000, 130_000)
        expiry.refresh(); runCurrent()
        advanceTimeBy(30_000); runCurrent()
        assertEquals("second", clipboard.clip?.token)
        advanceTimeBy(30_000); runCurrent()
        assertEquals(1, clipboard.clears)
    }

    @Test fun accessExceptionDoesNotLoseCleanupOnReturn() = runTest {
        val clipboard = Clipboard()
        val expiry = controller(clipboard)
        expiry.refresh(); runCurrent()
        clipboard.fails = true
        advanceTimeBy(60_000); runCurrent()
        clipboard.fails = false
        expiry.onFocusChanged(true); runCurrent()
        assertEquals(1, clipboard.clears)
    }

    @Test fun transientExceptionWhileFocusedRetries() = runTest {
        val clipboard = Clipboard()
        val expiry = controller(clipboard)
        advanceTimeBy(60_000)
        clipboard.fails = true
        expiry.onFocusChanged(true); runCurrent()
        clipboard.fails = false
        advanceTimeBy(5_000); runCurrent()
        assertEquals(1, clipboard.clears)
    }

    @Test fun ownershipIsRecheckedImmediatelyBeforeClearing() = runTest {
        val clipboard = Clipboard()
        val expiry = PasswordClipboardExpiry(backgroundScope,
            { clipboard.read().also { clipboard.clip = PasswordClipExpiry("new-owner", 160_000, 160_000) } },
            clipboard::clear, { 160_000 }, { 160_000 })
        expiry.refresh(); runCurrent()
        assertEquals(0, clipboard.clears)
        assertEquals("new-owner", clipboard.clip?.token)
    }

    @Test fun clockChangesAndRebootCannotExtendExpiry() {
        val expiry = PasswordClipExpiry("owner", 100_000, 100_000)
        assertEquals(0L, expiry.remaining(99_999, 110_000))
        assertEquals(0L, expiry.remaining(110_000, 1))
        assertEquals(0L, expiry.remaining(160_000, 110_000))
        assertEquals(0L, expiry.remaining(110_000, 160_000))
        assertEquals(50_000L, expiry.remaining(110_000, 105_000))
    }
}
