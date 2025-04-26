package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.core.domain.model.ValidationStatus
import com.sepideh.lilo.task.presentation.model.Category
import com.sepideh.lilo.task.presentation.model.Priority
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.description_label
import lilo.composeapp.generated.resources.title_label

data class TaskDetailState(
    val categories: List<Category> = emptyList(),
    val selectedCategory : Category?= null,
    val selectedPriority : Priority = Priority.priorities.first(),
    val isCategoryDialogOpen :Boolean = false,
    val isPriorityDialogOpen :Boolean = false,
    val isDateDialogOpen :Boolean = false,
    val isTimeDialogOpen :Boolean = false,
    val addCategoryOpen :Boolean = false,
    val titleError : ValidationStatus = ValidationStatus(args = arrayOf(Res.string.title_label)),
    val descriptionError : ValidationStatus = ValidationStatus(args = arrayOf(Res.string.description_label)),
    val hasAlarmPermission: Boolean = true,
    val hasNotificationPermission: Boolean = true,
    val shouldShowPermissionDialog: Boolean = false
    )
