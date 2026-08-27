package com.sepideh.lilo.note.presentation.note_list

import com.sepideh.lilo.category.presentation.CategoryPresentation
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.presentation.note_list.model.NoteSortOrder

sealed interface NoteListAction : BaseAction {
    data class OnSortOrderChanged(val sortOrder: NoteSortOrder) : NoteListAction
    data class OnCategorySelected(val id: Long?) : NoteListAction
    data class OnSearchToggle(val open: Boolean) : NoteListAction
    data class OnSearchQueryChange(val query: String) : NoteListAction
    data class OnDeleteNoteIcon(val note: Note?) : NoteListAction
    data object OnDismissDeleteDialog : NoteListAction
    data object OnDeleteNoteConfirm : NoteListAction
}


data class NoteListState(
    val sortOrder: NoteSortOrder = NoteSortOrder.Date,
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
    val notesResult: List<Note> = emptyList(),
    val categories: List<CategoryPresentation> = emptyList(),
    val isDeleteDialogOpen: Boolean = false,
    val selectedCategory: Long? = null,
)