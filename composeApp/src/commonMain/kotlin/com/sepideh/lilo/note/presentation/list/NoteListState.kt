package com.sepideh.lilo.note.presentation.list

import com.sepideh.lilo.core.presentation.components.group.GroupOption
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.presentation.list.model.NoteSortOrder

data class NoteListState(
    val sortOrder: NoteSortOrder = NoteSortOrder.Date,
    val draftSort: NoteSortOrder = NoteSortOrder.Date,
    val isSortSheetOpen: Boolean = false,
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
