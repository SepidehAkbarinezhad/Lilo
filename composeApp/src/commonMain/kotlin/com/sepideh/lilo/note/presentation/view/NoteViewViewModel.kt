package com.sepideh.lilo.note.presentation.view

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.note.domain.repository.NoteGroupRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteViewViewModel(
    private val noteRepository: NoteRepository,
    private val groupRepository: NoteGroupRepository,
) : BaseViewModel() {
    private val local = MutableStateFlow(NoteViewState())
    val state = local.asStateFlow()
    private var noteId: Long? = null
    private var loadJob: Job? = null

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            is NoteViewAction.Load -> {
                noteId = action.id
                load(action.id)
            }
            NoteViewAction.Retry -> noteId?.let(::load)
            NoteViewAction.Edit -> if (local.value.canEdit) {
                super.onAction(BaseAction.OnNavigateTo(AppRoutes.Notes.Detail(requireNotNull(local.value.note).id)))
            }
            NoteViewAction.Back -> super.onAction(BaseAction.OnNavigateTo(null))
        }
    }

    private fun load(id: Long) {
        loadJob?.cancel()
        local.update {
            val note = it.note?.takeIf { note -> note.id == id }
            NoteViewState(note = note, group = it.group.takeIf { note != null })
        }
        loadJob = viewModelScope.launch {
            try {
                val note = noteRepository.getNoteById(id)
                val group = note?.groupId?.let { groupRepository.getGroupById(it) }
                local.value = NoteViewState(note = note, group = group, isLoading = false, noteMissing = note == null)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                local.value = NoteViewState(isLoading = false, hasError = true)
            }
        }
    }

    override fun onResetState() = Unit
}
