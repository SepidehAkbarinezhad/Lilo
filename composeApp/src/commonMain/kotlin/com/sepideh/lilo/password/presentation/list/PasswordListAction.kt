package com.sepideh.lilo.password.presentation.list

import com.sepideh.lilo.password.domain.crypto.UnlockResult

import com.sepideh.lilo.core.presentation.BaseAction

sealed interface PasswordListAction : BaseAction {
    data object Retry : PasswordListAction
    data class OnReveal(val id: String) : PasswordListAction
    data class OnEdit(val id: String) : PasswordListAction
    data class OnCopy(val id: String) : PasswordListAction
    data class OnAuthenticated(val result: UnlockResult) : PasswordListAction
    data object OnHide : PasswordListAction
    data class OnQueryChanged(val value: String) : PasswordListAction
    data class OnSearchChanged(val visible: Boolean) : PasswordListAction
    data class OnSortOpen(val open: Boolean) : PasswordListAction
    data class OnSortChanged(val value: Boolean) : PasswordListAction
    data class OnServicesChanged(val ids: Set<String>) : PasswordListAction
    data class OnServiceSelected(val id: String?) : PasswordListAction
    data object OnResetFilters : PasswordListAction
    data object OnApplySort : PasswordListAction
    data class OnDeleteSelected(val id: String?) : PasswordListAction
    data object OnDeleteConfirm : PasswordListAction
}
