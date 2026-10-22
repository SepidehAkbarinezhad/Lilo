package com.sepideh.lilo.note.presentation.detail

import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.core.presentation.BaseAction

sealed interface NoteDetailAction : BaseAction {
    data class Load(val id: Long?) : NoteDetailAction
    data class Title(val value: String) : NoteDetailAction
    data class Content(val value: String) : NoteDetailAction
    data object PickImages : NoteDetailAction
    data class Images(val sources: List<ImageSource>) : NoteDetailAction
    data class RemoveImage(val image: com.sepideh.lilo.core.presentation.images.AttachmentPreview) : NoteDetailAction
    data object ClearImages : NoteDetailAction
    data object ImageError : NoteDetailAction
    data object Save : NoteDetailAction
    data object Back : NoteDetailAction
    data object Discard : NoteDetailAction
    data object KeepEditing : NoteDetailAction
    data object OpenGroups : NoteDetailAction
    data object DismissGroups : NoteDetailAction
    data class DraftGroup(val id: Long?) : NoteDetailAction
    data object ConfirmGroup : NoteDetailAction
    data object ManageGroups : NoteDetailAction
    data object CloseManagement : NoteDetailAction
    data class AddGroup(val title: String) : NoteDetailAction
    data class RenameGroup(val id: Long, val title: String) : NoteDetailAction
    data class DeleteGroup(val id: Long) : NoteDetailAction
    data object Retry : NoteDetailAction
}
