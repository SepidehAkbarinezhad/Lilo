package com.sepideh.lilo.task.presentation.detail

import com.sepideh.lilo.core.presentation.validation.ValidationStatus
import com.sepideh.lilo.task.presentation.TaskGroupUi
import com.sepideh.lilo.task.presentation.model.Priority
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.description_label
import lilo.composeapp.generated.resources.title_label

data class TaskDetailState(
    val task: com.sepideh.lilo.task.domain.model.Task = com.sepideh.lilo.task.domain.model.Task(priority = 1),
    val reminderEditorOpen: Boolean = false,
    val groupManagementOpen: Boolean = false,
    val draftGroupId: Long? = null,
    val isSaving: Boolean = false,
    val selectedImages: List<com.sepideh.lilo.core.domain.images.ImageSource>? = null,
    val isLoading: Boolean = false,
    val isAddingGroup: Boolean = false,
    val groupAddedVersion: Int = 0,
    val hasError: Boolean = false,
    val groups: List<TaskGroupUi> = emptyList(),
    val selectedGroup : TaskGroupUi?=null,
    val selectedPriority : Priority = Priority.getById(1),
    val groupDialogOpen :Boolean = false,
    val addGroupOpen :Boolean = false,
    val titleError : ValidationStatus = ValidationStatus(args = arrayOf(Res.string.title_label)),
    val descriptionError : ValidationStatus = ValidationStatus(args = arrayOf(Res.string.description_label)),
    val missingPermission: com.sepideh.lilo.task.domain.reminder.ReminderPermission? = null,
    val confirmSaveWithoutReminder: Boolean = false,
    val awaitingPermissionReturn: Boolean = false,
    )
