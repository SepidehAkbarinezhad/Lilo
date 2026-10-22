package com.sepideh.lilo.password.presentation.list

import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest
import com.sepideh.lilo.password.domain.crypto.UnlockResult


data class PasswordListState(
    val loading: Boolean = true,
    val serviceId: String? = null, val draftServiceId: String? = null,
    val records: List<Password> = emptyList(),
    val query: String = "", val searchVisible: Boolean = false,
    val alphabetical: Boolean = false, val sortOpen: Boolean = false, val draftAlphabetical: Boolean = false,
    val deleteId: String? = null, val error: Boolean = false,
    val request: PasswordUnlockRequest? = null, val busyId: String? = null,
    val revealedId: String? = null, val revealedSecret: String = "",
    val authError: UnlockResult? = null, val copiedId: String? = null,
)
