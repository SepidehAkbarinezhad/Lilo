package com.sepideh.lilo.password.presentation

import androidx.compose.runtime.Composable

import com.sepideh.lilo.password.domain.crypto.UnlockResult
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest

@Composable expect fun rememberPasswordUnlocker(onResult: (UnlockResult) -> Unit): (PasswordUnlockRequest) -> Unit

/** App-scoped: leaving Passwords must not stop background locking. */
@Composable expect fun PasswordSessionEffect()
