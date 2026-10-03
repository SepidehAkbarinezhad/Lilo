package com.sepideh.lilo.task.presentation.detail

import com.sepideh.lilo.core.presentation.validation.ValidationStatus
import com.sepideh.lilo.category.presentation.CategoryPresentation
import com.sepideh.lilo.task.presentation.model.Priority
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.description_label
import lilo.composeapp.generated.resources.title_label

data class TaskDetailState(
    val task: com.sepideh.lilo.task.domain.model.Task = com.sepideh.lilo.task.domain.model.Task(priority = 1),
    val reminderEditorOpen: Boolean = false,
    val groupManagementOpen: Boolean = false,
    val draftCategoryId: Long? = null,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val isAddingGroup: Boolean = false,
    val groupAddedVersion: Int = 0,
    val hasError: Boolean = false,
    val categories: List<CategoryPresentation> = emptyList(),
    val selectedCategory : CategoryPresentation?=null,
    val selectedPriority : Priority = Priority.getById(1),
    val categoryDialogOpen :Boolean = false,
    val priorityDialogOpen :Boolean = false,
    val addCategoryOpen :Boolean = false,
    val titleError : ValidationStatus = ValidationStatus(args = arrayOf(Res.string.title_label)),
    val descriptionError : ValidationStatus = ValidationStatus(args = arrayOf(Res.string.description_label)),
    val shouldShowPermissionDeniedDialog: Boolean = false,
    )
