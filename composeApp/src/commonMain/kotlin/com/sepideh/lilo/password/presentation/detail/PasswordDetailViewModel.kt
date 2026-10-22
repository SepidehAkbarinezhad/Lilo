package com.sepideh.lilo.password.presentation.detail

import com.sepideh.lilo.password.domain.crypto.DeviceLockRequiredException
import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.repository.PasswordRepository
import com.sepideh.lilo.password.domain.crypto.VaultCrypto
import com.sepideh.lilo.password.domain.model.normalizePasswordWebsite
import com.sepideh.lilo.password.domain.model.secretTooLong
import com.sepideh.lilo.password.domain.model.titleTooLong

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PasswordDetailViewModel(private val repository: PasswordRepository, private val crypto: VaultCrypto) : BaseViewModel() {
    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            is PasswordDetailAction.OnInitialize -> initialize(action.id)
            PasswordDetailAction.Retry -> load()
            is PasswordDetailAction.OnTitleChanged -> edit { it.copy(title = action.value) }
            is PasswordDetailAction.OnUsernameChanged -> edit { it.copy(username = action.value) }
            is PasswordDetailAction.OnSecretChanged -> edit { it.copy(secret = action.value) }
            is PasswordDetailAction.OnWebsiteChanged -> edit { it.copy(website = action.value) }
            is PasswordDetailAction.OnNotesChanged -> edit { it.copy(notes = action.value) }
            is PasswordDetailAction.OnServiceSelected -> edit { it.copy(serviceId = action.id) }
            PasswordDetailAction.OnVisibility -> visibility()
            PasswordDetailAction.OnSave -> save()
            PasswordDetailAction.Back -> back()
            PasswordDetailAction.KeepEditing -> keepEditing()
            PasswordDetailAction.Discard -> discard()
        }
    }

    private val local = MutableStateFlow(PasswordDetailState())
    val state = local.asStateFlow()
    private var id: String? = null
    private var initialized = false
    private var original = Password()
    private var operation: Job? = null
    private var observedSession = crypto.session.value

    init {
        viewModelScope.launch { crypto.session.collect { session ->
            val changed = session != observedSession
            observedSession = session
            if (initialized && changed) {
                operation?.cancel()
                if (id == null) {
                    if (!session.unlocked) local.update { it.copy(record = it.record.copy(secret = "", notes = ""),
                        saving = false, visible = true, discardOpen = false) }
                } else if (!session.unlocked) {
                    if (!local.value.locked) {
                        leaveLockedEditor()
                    } else load()
                } else load()
            }
        } }
    }

    private fun initialize(value: String?) {
        if (initialized) return
        id = value
        initialized = true
        if (id == null) local.value = PasswordDetailState(locked = false, visible = true)
        else load()
    }

    private fun load() {
        val recordId = id ?: return
        operation?.cancel()
        val session = crypto.session.value
        local.update { it.copy(loading = true, error = false, loadFailed = false) }
        operation = viewModelScope.launch {
            try {
                val request = repository.unlockRequest(recordId)
                if (crypto.session.value != session) return@launch
                if (session.unlocked && session.recordId == recordId) {
                    val record = requireNotNull(repository.get(recordId))
                    if (crypto.session.value == session) {
                        original = record
                        local.update { it.copy(record = record, request = request, locked = false, loading = false, visible = true) }
                    }
                } else leaveLockedEditor()
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { if (crypto.session.value == session) local.update { it.copy(error = true, loadFailed = true, loading = false) } }
        }
    }

    private fun leaveLockedEditor() {
        original = Password()
        local.value = PasswordDetailState()
        initialized = false
        onAction(BaseAction.OnNavigateTo(null))
    }

    private fun edit(transform: (Password) -> Password) {
        val ui = local.value
        if (ui.locked || ui.loading || ui.saving || ui.loadFailed) return
        local.update { it.copy(error = false, setupRequired = false, record = transform(it.record), serviceInvalid = false, websiteInvalid = false, secretInvalid = false, identifierInvalid = false) }
    }
    private fun visibility() { if (!local.value.locked) local.update { it.copy(visible = !it.visible) } }

    private fun save() {
        val ui = local.value
        val session = crypto.session.value
        if (ui.locked || ui.loading || ui.saving || ui.loadFailed) return
        if (id != null && (!session.unlocked || session.recordId != id)) return
        if (!ui.record.hasIdentifier || ui.record.serviceId.isNullOrBlank() || ui.record.secret.isEmpty() || ui.record.titleTooLong || ui.record.secretTooLong || normalizePasswordWebsite(ui.record.website) == null) {
            local.update { it.copy(identifierInvalid = !ui.record.hasIdentifier, serviceInvalid = ui.record.serviceId.isNullOrBlank(), websiteInvalid = normalizePasswordWebsite(ui.record.website) == null, secretInvalid = ui.record.secret.isEmpty()) }
            return
        }
        local.update { it.copy(saving = true, error = false, setupRequired = false) }
        operation = viewModelScope.launch {
            try {
                val saved = repository.save(ui.record)
                if (crypto.session.value == session) {
                    original = saved
                    local.update { it.copy(record = saved, saving = false, visible = false) }
                    onAction(BaseAction.OnNavigateTo(null))
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: DeviceLockRequiredException) { local.update { it.copy(error = true, setupRequired = true, saving = false) } }
            catch (_: Exception) { if (crypto.session.value == session) local.update { it.copy(error = true, saving = false) } }
        }
    }

    private fun back() {
        val ui = local.value
        if (ui.saving) return
        if (!ui.locked && ui.record != original) local.update { it.copy(discardOpen = true) }
        else discard()
    }
    private fun keepEditing() { local.update { it.copy(discardOpen = false) } }
    private fun discard() {
        operation?.cancel()
        local.value = PasswordDetailState()
        original = Password()
        onAction(BaseAction.OnNavigateTo(null))
    }
    override fun onResetState() {
        operation?.cancel()
        local.value = PasswordDetailState()
        original = Password()
        initialized = false
    }
}
