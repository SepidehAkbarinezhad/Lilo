package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.task.presentation.model.Category
import com.sepideh.lilo.task.presentation.model.Priority

data class TaskDetailState(
    val categories: List<Category> = emptyList(),
    val selectedCategory : Category?= null,
    val priorities: List<Priority> = Priority.priorities
    )
