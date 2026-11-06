package com.sepideh.lilo.password.presentation

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.withResumed
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest
import com.sepideh.lilo.password.domain.crypto.UnlockResult

internal suspend fun deliverCredentialUnlockResult(
    lifecycle: Lifecycle,
    confirmed: Boolean,
    request: PasswordUnlockRequest?,
    unlock: (PasswordUnlockRequest) -> UnlockResult,
    onResult: (UnlockResult) -> Unit,
) {
    if (!confirmed || request == null) {
        onResult(UnlockResult.CANCELLED)
        return
    }

    // Activity results can arrive before the password destination starts again.
    // Unwrap and notify together, only once it resumes. Disposal cancels this wait.
    lifecycle.withResumed {
        onResult(unlock(request))
    }
}
