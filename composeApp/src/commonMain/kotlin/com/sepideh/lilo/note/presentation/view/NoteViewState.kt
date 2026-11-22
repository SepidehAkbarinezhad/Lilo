package com.sepideh.lilo.note.presentation.view

import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.model.NoteGroup

data class NoteViewState(
    val note: Note? = null,
    val group: NoteGroup? = null,
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val noteMissing: Boolean = false,
) {
    val canEdit: Boolean get() = note != null && !isLoading && !hasError && !noteMissing
}
