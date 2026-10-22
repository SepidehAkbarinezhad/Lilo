package com.sepideh.lilo.password.presentation.components

import com.sepideh.lilo.password.domain.model.PasswordService
import com.sepideh.lilo.password.domain.repository.PasswordServiceRepository

import com.sepideh.lilo.core.domain.validation.isShortNameTooLong
import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import com.sepideh.lilo.core.presentation.BaseAction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PasswordServicesViewModel(private val repository: PasswordServiceRepository) : ViewModel() {
    fun onAction(action: BaseAction) {
        when (action) {
            is PasswordServicesAction.OnOpen -> open(action.id)
            PasswordServicesAction.OnClose -> close()
            PasswordServicesAction.OnManage -> manage()
            PasswordServicesAction.OnCloseManagement -> closeManagement()
            is PasswordServicesAction.OnDraftSelected -> selectDraft(action.id)
            PasswordServicesAction.OnConfirmSelection -> confirmSelection()
            is PasswordServicesAction.OnQueryChanged -> query(action.value)
            is PasswordServicesAction.OnNameChanged -> name(action.value)
            is PasswordServicesAction.OnEdit -> edit(action.service)
            PasswordServicesAction.OnCancelEdit -> cancelEdit()
            PasswordServicesAction.OnConsumeRemoval -> consumeRemoval()
            PasswordServicesAction.OnSave -> save()
            is PasswordServicesAction.OnDeleteSelected -> askDelete(action.service)
            is PasswordServicesAction.OnReplacementSelected -> replacement(action.id)
            PasswordServicesAction.OnCancelDelete -> cancelDelete()
            PasswordServicesAction.OnDeleteConfirm -> delete()
        }
    }

    private val local = MutableStateFlow(PasswordServicesState())
    val state = local.asStateFlow()
    init { viewModelScope.launch {
        repository.observe().catch { local.update { it.copy(error = true) } }.collect { services ->
            local.update { it.copy(loaded = true, services = services.sortedBy { service -> service.name.lowercase() }) }
        }
    } }
    private fun open(selectedId: String?) { local.update { it.copy(open = true, managing = false, draftId = selectedId, query = "", error = false, duplicateName = false) } }
    private fun close() { if (!local.value.busy) local.update { it.copy(open = false, managing = false, editorOpen = false, deleting = null) } }
    private fun manage() { local.update { it.copy(managing = true, query = "", editorOpen = false, error = false, duplicateName = false) } }
    private fun closeManagement() { if (!local.value.busy) local.update { it.copy(managing = false, editorOpen = false, deleting = null, error = false, duplicateName = false) } }
    private fun selectDraft(id: String) { local.update { it.copy(draftId = id) } }
    private fun confirmSelection() { local.update { it.copy(open = false, managing = false) } }
    private fun query(value: String) { local.update { it.copy(query = value) } }
    private fun name(value: String) { local.update { it.copy(name = value, error = false, duplicateName = false) } }
    private fun edit(service: PasswordService? = null) {
        if (service?.isDefault == true) return
        local.update { it.copy(editorOpen = true, editingId = service?.id, name = service?.name.orEmpty(), error = false, duplicateName = false) }
    }
    private fun cancelEdit() { local.update { it.copy(editorOpen = false, error = false, duplicateName = false) } }
    private fun consumeRemoval() { local.update { it.copy(removedId = null, movedToId = null) } }
    private fun operation(block: suspend () -> Unit) {
        if (local.value.busy) return
        local.update { it.copy(busy = true, error = false, duplicateName = false) }
        viewModelScope.launch {
            try { block() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: DuplicateNameException) { local.update { it.copy(duplicateName = true) } }
            catch (_: Exception) { local.update { it.copy(error = true) } }
            finally { local.update { it.copy(busy = false) } }
        }
    }
    private fun save() {
        val ui = local.value
        if (ui.name.isBlank() || isShortNameTooLong(ui.name)) return
        operation {
            if (ui.editingId == null) {
                val created = repository.create(ui.name)
                local.update { it.copy(draftId = created.id, query = "", editorOpen = false) }
            } else {
                repository.rename(ui.editingId, ui.name)
                local.update { it.copy(editorOpen = false) }
            }
        }
    }
    private fun askDelete(service: PasswordService) {
        if (service.isDefault) return
        operation {
            val count = repository.usage(service.id)
            local.update { it.copy(deleting = service, usage = count, replacement = null, editorOpen = false) }
        }
    }
    private fun replacement(id: String) { local.update { it.copy(replacement = id) } }
    private fun cancelDelete() { if (!local.value.busy) local.update { it.copy(deleting = null, error = false, duplicateName = false) } }
    private fun delete() {
        val ui = local.value
        val service = ui.deleting ?: return
        if (service.isDefault) return
        operation {
            repository.delete(service.id, ui.replacement)
            local.update { it.copy(deleting = null, replacement = null, draftId = if (ui.draftId == service.id) ui.replacement else ui.draftId, removedId = service.id, movedToId = ui.replacement) }
        }
    }
}
