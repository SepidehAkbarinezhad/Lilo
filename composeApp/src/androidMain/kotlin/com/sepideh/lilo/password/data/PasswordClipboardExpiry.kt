package com.sepideh.lilo.password.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

/** Metadata lives with the OS clipboard, so process recreation does not lose the deadline. */
internal data class PasswordClipExpiry(val token: String, val wallTime: Long, val elapsedTime: Long) {
    fun remaining(wallNow: Long, elapsedNow: Long): Long {
        // A clock rollback or reboot must not extend the lifetime of a secret.
        if (wallNow < wallTime || elapsedNow < elapsedTime) return 0
        return (LIFETIME - maxOf(wallNow - wallTime, elapsedNow - elapsedTime)).coerceAtLeast(0)
    }

    companion object { const val LIFETIME = 60_000L }
}

/** Main-thread confined. Only metadata, never the copied password, enters a delayed job. */
internal class PasswordClipboardExpiry(
    private val scope: CoroutineScope,
    private val read: () -> PasswordClipExpiry?,
    private val clearIfOwned: (String) -> Unit,
    private val wallNow: () -> Long,
    private val elapsedNow: () -> Long,
) {
    private var job: Job? = null
    private var focused = false

    fun onFocusChanged(hasFocus: Boolean) {
        focused = hasFocus
        if (hasFocus) refresh()
    }

    fun refresh() {
        job?.cancel()
        job = scope.launch {
            do {
                var wait = 5_000L
                try {
                    val expiry = read() ?: return@launch
                    val remaining = expiry.remaining(wallNow(), elapsedNow())
                    if (remaining == 0L) {
                        clearIfOwned(expiry.token)
                        return@launch
                    }
                    wait = remaining
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (_: Exception) {
                    // Keep metadata in the clipboard. Retry while focused, or on the next focus gain.
                    if (!focused) return@launch
                }
                delay(wait)
                // Always attempt the deadline once, including on older Android in the background.
            } while (true)
        }
    }
}
