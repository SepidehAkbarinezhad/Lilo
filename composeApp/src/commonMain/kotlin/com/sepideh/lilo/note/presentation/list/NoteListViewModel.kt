package com.sepideh.lilo.note.presentation.list

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.note.domain.model.*
import com.sepideh.lilo.note.domain.repository.NoteGroupRepository
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.note.domain.usecase.NoteMutations
import com.sepideh.lilo.note.presentation.toGroupOptions
import com.sepideh.lilo.note.presentation.list.model.NoteSortOrder
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class NoteListViewModel(
    languageProvider: LanguageProvider,
    noteRepository: NoteRepository,
    groupRepository: NoteGroupRepository,
    private val mutations: NoteMutations,
) : BaseViewModel() {
    private val local = MutableStateFlow(NoteListState())
    private val refresh = MutableStateFlow(0)
    private val query = local.map { it.searchQuery }.distinctUntilChanged().debounce(250)
    private val notes = refresh.flatMapLatest {
        noteRepository.getAllNotes().onEach { local.update { ui -> ui.copy(isLoading = false) } }
            .catch { local.update { ui -> ui.copy(isLoading = false, hasError = true) }; emit(emptyList()) }
    }
    private val groups = refresh.flatMapLatest {
        groupRepository.getAllGroups().catch { local.update { ui -> ui.copy(hasError = true) }; emit(emptyList()) }
    }
    val state = combine(local, notes, groups, query, languageProvider.languageFlow) { ui, notes, groups, text, language ->
        val selected = ui.selectedGroup?.takeIf { id -> id == 0L || groups.any { it.id == id } }
        val effectiveNotes = notes.map { note -> note.copy(groupId = note.groupId?.takeIf { id -> groups.any { it.id == id } }) }
        ui.copy(groups = groups.toGroupOptions(language), selectedGroup = selected,
            notesResult = effectiveNotes.matching(NoteQuery(text, selected,
                if (ui.sortOrder == NoteSortOrder.Title) NoteSort.TITLE else NoteSort.UPDATED)))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NoteListState())

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            is NoteListAction.Search -> local.update { it.copy(searchQuery = action.query) }
            is NoteListAction.SearchVisible -> local.update { it.copy(isSearchVisible = action.visible) }
            is NoteListAction.SelectGroup -> local.update { it.copy(selectedGroup = action.id) }
            NoteListAction.OpenSort -> local.update { it.copy(isSortSheetOpen = true, draftSort = it.sortOrder) }
            NoteListAction.DismissSort -> local.update { it.copy(isSortSheetOpen = false, draftSort = it.sortOrder) }
            is NoteListAction.DraftSort -> local.update { it.copy(draftSort = action.order) }
            NoteListAction.ApplySort -> local.update { it.copy(isSortSheetOpen = false, sortOrder = it.draftSort) }
            is NoteListAction.OnDeleteNoteIcon -> if (!local.value.isDeleting) local.update { it.copy(selectedNote = action.note, isDeleteDialogOpen = true) }
            NoteListAction.DismissDelete -> if (!local.value.isDeleting) local.update { it.copy(isDeleteDialogOpen = false, selectedNote = null) }
            NoteListAction.ConfirmDelete -> deleteSelected()
            NoteListAction.Retry -> { local.update { it.copy(hasError = false, isLoading = true) }; refresh.update { it + 1 } }
        }
    }
    private fun deleteSelected() {
        val id = local.value.selectedNote?.id ?: return
        if (local.value.isDeleting) return
        local.update { it.copy(isDeleting = true, hasError = false, isDeleteDialogOpen = false) }
        viewModelScope.launch {
            try {
                mutations.delete(id)
                local.update { it.copy(isDeleting = false, selectedNote = null) }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { local.update { it.copy(isDeleting = false, hasError = true) } }
        }
    }
    override fun onResetState() = Unit
}
