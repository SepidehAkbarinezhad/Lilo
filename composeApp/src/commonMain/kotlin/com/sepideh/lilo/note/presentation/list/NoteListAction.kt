package com.sepideh.lilo.note.presentation.list

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.presentation.list.model.NoteSortOrder

sealed interface NoteListAction : BaseAction {
    data class OnDeleteNoteIcon(val note: Note) : NoteListAction
    data object ConfirmDelete : NoteListAction
    data object DismissDelete : NoteListAction
    data class Search(val query: String) : NoteListAction
    data class SearchVisible(val visible: Boolean) : NoteListAction
    data class SelectGroup(val id: Long?) : NoteListAction
    data object OpenSort : NoteListAction
    data object DismissSort : NoteListAction
    data class DraftSort(val order: NoteSortOrder) : NoteListAction
    data object ApplySort : NoteListAction
    data object Retry : NoteListAction
}
