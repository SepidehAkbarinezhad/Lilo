package com.sepideh.lilo.note.presentation.detail

import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.core.presentation.components.group.GroupOption
import com.sepideh.lilo.core.presentation.validation.ValidationStatus
import com.sepideh.lilo.note.domain.model.Note

data class NoteDetailState(
    val isEditing: Boolean = false,
    val note: Note = Note(),
    val groups: List<GroupOption> = emptyList(),
    val removedImageNames: Set<String> = emptySet(),
    val selectedImages: List<ImageSource>? = null,
    val titleError: ValidationStatus = ValidationStatus(),
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isGroupWorking: Boolean = false,
    val groupAddedVersion: Int = 0,
    val hasError: Boolean = false,
    val duplicateGroupName: Boolean = false,
    val noteMissing: Boolean = false,
    val groupDialogOpen: Boolean = false,
    val groupManagementOpen: Boolean = false,
    val draftGroupId: Long? = null,
    val confirmDiscard: Boolean = false,
) {
    val selectedGroup: GroupOption? get() = groups.firstOrNull { it.id == note.groupId }
    val fieldsEnabled: Boolean get() = !isLoading && !isSaving && !isGroupWorking && !noteMissing
}
