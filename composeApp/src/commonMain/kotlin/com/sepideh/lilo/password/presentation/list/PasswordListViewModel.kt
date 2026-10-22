package com.sepideh.lilo.password.presentation.list

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.password.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PasswordListViewModel(private val repository: PasswordRepository, private val crypto: VaultCrypto,
    private val clipboard: PasswordClipboard) : BaseViewModel() {
    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            PasswordListAction.Retry -> observe()
            is PasswordListAction.OnReveal -> open(action.id)
            is PasswordListAction.OnEdit -> open(action.id, edit = true)
            is PasswordListAction.OnCopy -> copy(action.id)
            is PasswordListAction.OnAuthenticated -> authenticated(action.result)
            PasswordListAction.OnHide -> hide()
            is PasswordListAction.OnQueryChanged -> query(action.value)
            is PasswordListAction.OnSearchChanged -> search(action.visible)
            is PasswordListAction.OnSortOpen -> sortOpen(action.open)
            is PasswordListAction.OnSortChanged -> sort(action.value)
            is PasswordListAction.OnServicesChanged -> reconcileServices(action.ids)
            is PasswordListAction.OnServiceSelected -> service(action.id)
            PasswordListAction.OnResetFilters -> resetFilters()
            PasswordListAction.OnApplySort -> applySort()
            is PasswordListAction.OnDeleteSelected -> selectDelete(action.id)
            PasswordListAction.OnDeleteConfirm -> delete()
        }
    }

    private val local = MutableStateFlow(PasswordListState())
    val state = local.asStateFlow()
    private var observing: Job? = null
    private var opening: Job? = null
    private enum class OpenAction { REVEAL, EDIT, COPY }
    private var openAction = OpenAction.REVEAL
    private var revealedSession: VaultSession? = null
    private var copyFeedback: Job? = null

    init {
        observe()
        viewModelScope.launch { crypto.session.collect { session ->
            if (!session.unlocked || session != revealedSession) {
                revealedSession = null
                local.update { it.copy(revealedId = null, revealedSecret = "") }
            }
        } }
    }

    private fun observe() {
        observing?.cancel()
        local.update { it.copy(loading = true, error = false) }
        observing = viewModelScope.launch {
            repository.observePasswords().catch { local.update { it.copy(error = true, loading = false) } }
                .collect { records -> local.update { it.copy(records = records, loading = false, error = false) } }
        }
    }

    private fun open(id: String, edit: Boolean = false) {
        if (local.value.busyId != null) return
        if (!edit && local.value.revealedId == id) { hide(); return }
        beginUnlock(id, if (edit) OpenAction.EDIT else OpenAction.REVEAL)
    }

    private fun copy(id: String) {
        if (local.value.busyId != null) return
        val session = crypto.session.value
        if (local.value.revealedId == id && session == revealedSession && session.unlocked && session.recordId == id) {
            copySecret(id, local.value.revealedSecret, session)
        } else beginUnlock(id, OpenAction.COPY)
    }

    private fun beginUnlock(id: String, action: OpenAction) {
        hide()
        openAction = action
        local.update { it.copy(busyId = id, authError = null, error = false, copiedId = null) }
        opening = viewModelScope.launch {
            try { val request = repository.unlockRequest(id); local.update { it.copy(request = request) } }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { local.update { it.copy(busyId = null, error = true) } }
        }
    }

    private fun authenticated(result: UnlockResult) {
        val id = local.value.busyId ?: return
        local.update { it.copy(request = null) }
        if (result != UnlockResult.SUCCESS) {
            crypto.lock()
            local.update { it.copy(busyId = null, authError = result.takeUnless { it == UnlockResult.CANCELLED }) }
            return
        }
        val session = crypto.session.value
        if (!session.unlocked || session.recordId != id) {
            crypto.lock()
            local.update { it.copy(busyId = null, authError = UnlockResult.FAILED) }; return
        }
        opening = viewModelScope.launch {
            try {
                val record = requireNotNull(repository.get(id))
                if (crypto.session.value != session) return@launch
                currentCoroutineContext().ensureActive()
                local.update { it.copy(busyId = null, authError = null) }
                when (openAction) {
                    OpenAction.REVEAL -> {
                        revealedSession = session
                        local.update { it.copy(revealedId = id, revealedSecret = record.secret) }
                    }
                    OpenAction.EDIT -> onAction(BaseAction.OnNavigateTo(AppRoutes.Passwords.Detail(id)))
                    OpenAction.COPY -> try { copySecret(id, record.secret, session) } finally { crypto.lock() }
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { local.update { it.copy(busyId = null, authError = UnlockResult.FAILED) } }
            finally { local.update { it.copy(busyId = null) } }
        }
    }

    private fun copySecret(id: String, secret: String, session: VaultSession) {
        if (!session.unlocked || session.recordId != id || crypto.session.value != session) return
        try {
            clipboard.copyPassword(secret)
            copyFeedback?.cancel()
            local.update { it.copy(copiedId = id, error = false) }
            copyFeedback = viewModelScope.launch {
                delay(2_000)
                local.update { it.copy(copiedId = null) }
            }
        } catch (_: Exception) { local.update { it.copy(error = true, copiedId = null) } }
    }

    private fun hide() {
        revealedSession = null
        local.update { it.copy(revealedId = null, revealedSecret = "") }
        crypto.lock()
    }
    private fun query(value: String) { local.update { it.copy(query = value) } }
    private fun search(visible: Boolean) { local.update { it.copy(searchVisible = visible, query = if (visible) it.query else "") } }
    private fun sortOpen(open: Boolean) { local.update { it.copy(sortOpen = open, draftAlphabetical = it.alphabetical, draftServiceId = it.serviceId) } }
    private fun sort(value: Boolean) { local.update { it.copy(draftAlphabetical = value) } }
    private fun reconcileServices(ids: Set<String>) { local.update { it.copy(
        serviceId = it.serviceId?.takeIf(ids::contains), draftServiceId = it.draftServiceId?.takeIf(ids::contains)) } }
    private fun service(id: String?) { local.update { it.copy(draftServiceId = id) } }
    private fun resetFilters() { local.update { it.copy(draftServiceId = null, draftAlphabetical = false) } }
    private fun applySort() { local.update { it.copy(alphabetical = it.draftAlphabetical, serviceId = it.draftServiceId, sortOpen = false) } }
    private fun selectDelete(id: String?) { local.update { it.copy(deleteId = id) } }
    private fun delete() {
        val id = local.value.deleteId ?: return
        local.update { it.copy(deleteId = null) }
        if (local.value.revealedId == id) hide()
        viewModelScope.launch {
            try { repository.delete(id) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { local.update { it.copy(error = true) } }
        }
    }
    override fun onResetState() {
        opening?.cancel()
        copyFeedback?.cancel()
        // An authenticated editor navigation has already cleared busy/revealed state; retain its key.
        if (local.value.busyId != null || local.value.revealedId != null) crypto.lock()
        revealedSession = null
        local.update { it.copy(revealedId = null, revealedSecret = "", copiedId = null, request = null, busyId = null) }
    }
}
