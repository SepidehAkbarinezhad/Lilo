package com.sepideh.lilo.password.presentation.detail

import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.crypto.PasswordUnlockRequest


data class PasswordDetailState(
    val isEditing: Boolean = false,
    val services: com.sepideh.lilo.password.presentation.components.PasswordServicesState = com.sepideh.lilo.password.presentation.components.PasswordServicesState(),
    val locked: Boolean = true, val record: Password = Password(),
    val request: PasswordUnlockRequest? = null,
    val loadFailed: Boolean = false, val loading: Boolean = false, val saving: Boolean = false,
    val error: Boolean = false, val setupRequired: Boolean = false,
    val identifierInvalid: Boolean = false, val serviceInvalid: Boolean = false, val websiteInvalid: Boolean = false, val secretInvalid: Boolean = false,
    val visible: Boolean = false, val discardOpen: Boolean = false,
)
