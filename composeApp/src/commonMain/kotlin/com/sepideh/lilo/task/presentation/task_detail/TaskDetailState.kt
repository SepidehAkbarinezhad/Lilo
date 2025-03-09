package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.task.presentation.model.Category

data class TaskDetailState(
    val categories: List<Category> = emptyList(),
    val selectedCategory : Category= Category.categories[0]
    )
