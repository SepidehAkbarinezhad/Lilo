package com.sepideh.lilo.password.presentation.detail

import com.sepideh.lilo.core.presentation.BaseAction

sealed interface PasswordDetailAction : BaseAction {
    data class OnInitialize(val id: String?) : PasswordDetailAction
    data object Retry : PasswordDetailAction
    data class OnTitleChanged(val value: String) : PasswordDetailAction
    data class OnUsernameChanged(val value: String) : PasswordDetailAction
    data class OnSecretChanged(val value: String) : PasswordDetailAction {
        override fun toString(): String = "OnSecretChanged(redacted)"
    }
    data class OnWebsiteChanged(val value: String) : PasswordDetailAction
    data class OnNotesChanged(val value: String) : PasswordDetailAction {
        override fun toString(): String = "OnNotesChanged(redacted)"
    }
    data class OnServiceSelected(val id: String?) : PasswordDetailAction
    data object OnVisibility : PasswordDetailAction
    data object OnSave : PasswordDetailAction
    data object Back : PasswordDetailAction
    data object KeepEditing : PasswordDetailAction
    data object Discard : PasswordDetailAction
}
