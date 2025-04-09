package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.core.presentation.ValidationStatus
import com.sepideh.lilo.task.presentation.model.Category
import com.sepideh.lilo.task.presentation.model.Priority
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.title_label

data class TaskDetailState(
    val categories: List<Category> = emptyList(),
    val selectedCategory : Category?= null,
    val selectedPriority : Priority = Priority.priorities.first(),
    val titleError : ValidationStatus = ValidationStatus(args = arrayOf(Res.string.title_label))
    )
