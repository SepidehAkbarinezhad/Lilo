package com.sepideh.lilo.note.presentation.list

import com.sepideh.lilo.category.presentation.CategoryPresentation
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.presentation.list.model.NoteSortOrder

sealed interface NoteListAction : BaseAction {
    data class OnDeleteNoteIcon(val note: Note?) : NoteListAction
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