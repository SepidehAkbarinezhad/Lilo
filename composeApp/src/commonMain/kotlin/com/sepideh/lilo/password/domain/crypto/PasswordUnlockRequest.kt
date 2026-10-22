package com.sepideh.lilo.password.domain.crypto

data class PasswordUnlockRequest(val id: String, val sealed: SealedPassword)
