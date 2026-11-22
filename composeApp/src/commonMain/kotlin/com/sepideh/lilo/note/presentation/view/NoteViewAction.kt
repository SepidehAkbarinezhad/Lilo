package com.sepideh.lilo.note.presentation.view

import com.sepideh.lilo.core.presentation.BaseAction

sealed interface NoteViewAction : BaseAction {
    data class Load(val id: Long) : NoteViewAction
    data object Retry : NoteViewAction
    data object Edit : NoteViewAction
    data object Back : NoteViewAction
}
