package com.sepideh.lilo.password.domain.clipboard

/** Copies a user-authorized password using the platform's sensitive clipboard protections. */
fun interface PasswordClipboard {
    fun copyPassword(value: String)
}
