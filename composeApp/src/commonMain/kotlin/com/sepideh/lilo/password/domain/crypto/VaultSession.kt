package com.sepideh.lilo.password.domain.crypto

data class VaultSession(val generation: Long = 0, val unlocked: Boolean = false, val recordId: String? = null)
