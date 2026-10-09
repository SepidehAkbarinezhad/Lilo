package com.sepideh.lilo.note.presentation.list

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.group.GroupOption
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.presentation.list.model.NoteSortOrder

sealed interface NoteListAction : BaseAction {
    data class OnDeleteNoteIcon(val note: Note) : NoteListAction
    data object ConfirmDelete : NoteListAction
    data object DismissDelete : NoteListAction
    data class Search(val query: String) : NoteListAction
    data class SearchVisible(val visible: Boolean) : NoteListAction
    data class SelectGroup(val id: Long?) : NoteListAction
    data object OpenFilters : NoteListAction
    data object DismissFilters : NoteListAction
    data class DraftSort(val order: NoteSortOrder) : NoteListAction
    data object ApplyFilters : NoteListAction
    data object ResetFilters : NoteListAction
    data object Retry : NoteListAction
}

data class NoteListState(
    val sortOrder: NoteSortOrder = NoteSortOrder.Date,
    val draftSort: NoteSortOrder = NoteSortOrder.Date,
    val isFilterSheetOpen: Boolean = false,
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
    val notesResult: List<Note> = emptyList(),
    val groups: List<GroupOption> = emptyList(),
    val selectedGroup: Long? = null,
    val selectedNote: Note? = null,
    val isDeleteDialogOpen: Boolean = false,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val hasError: Boolean = false,
)
