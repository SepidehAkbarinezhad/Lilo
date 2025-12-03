package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.category.domain.CategoryDomain
import com.sepideh.lilo.category.domain.toPresentation
import com.sepideh.lilo.core.domain.model.ValidationStatus
import com.sepideh.lilo.category.presentation.CategoryPresentation
import com.sepideh.lilo.task.presentation.model.Priority
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.description_label
import lilo.composeapp.generated.resources.title_label

data class TaskDetailState(
    val categories: List<CategoryPresentation> = emptyList(),
    val selectedCategory : CategoryPresentation,
    val selectedPriority : Priority = Priority.priorities.first(),
    val categoryDialogOpen :Boolean = false,
    val priorityDialogOpen :Boolean = false,
    val reminderDatePickerOpen :Boolean = false,
    val reminderTimePickerOpen :Boolean = false,
    val addCategoryOpen :Boolean = false,
    val titleError : ValidationStatus = ValidationStatus(args = arrayOf(Res.string.title_label)),
    val descriptionError : ValidationStatus = ValidationStatus(args = arrayOf(Res.string.description_label)),
    val shouldShowPermissionDialog: Boolean = false,
    val shouldShowPermissionDeniedDialog: Boolean = false
    )
