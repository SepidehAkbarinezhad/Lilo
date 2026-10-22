package com.sepideh.lilo.password.presentation.components

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.password.domain.*

sealed interface PasswordServicesAction : BaseAction {
    data class OnOpen(val id: String?) : PasswordServicesAction
    data object OnClose : PasswordServicesAction
    data object OnManage : PasswordServicesAction
    data object OnCloseManagement : PasswordServicesAction
    data class OnDraftSelected(val id: String) : PasswordServicesAction
    data object OnConfirmSelection : PasswordServicesAction
    data class OnQueryChanged(val value: String) : PasswordServicesAction
    data class OnNameChanged(val value: String) : PasswordServicesAction
    data class OnEdit(val service: PasswordService? = null) : PasswordServicesAction
    data object OnCancelEdit : PasswordServicesAction
    data object OnConsumeRemoval : PasswordServicesAction
    data object OnSave : PasswordServicesAction
    data class OnDeleteSelected(val service: PasswordService) : PasswordServicesAction
    data class OnReplacementSelected(val id: String) : PasswordServicesAction
    data object OnCancelDelete : PasswordServicesAction
    data object OnDeleteConfirm : PasswordServicesAction
}
