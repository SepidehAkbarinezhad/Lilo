package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.task.presentation.model.Category
import com.sepideh.lilo.task.presentation.model.Priority

data class TaskDetailState(
    val categories: List<Category> = emptyList(),
    val selectedCategory : Category?= null,
    val selectedPriority : Priority = Priority.priorities.first(),
    val isCategoryDialogOpen :Boolean = false,
    val isPriorityDialogOpen :Boolean = false,
    val isDateDialogOpen :Boolean = false,
    val isTimeDialogOpen :Boolean = false,
    val addCategoryOpen :Boolean = false,
    )
