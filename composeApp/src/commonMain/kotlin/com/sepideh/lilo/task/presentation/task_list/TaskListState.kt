package com.sepideh.lilo.task.presentation.task_list

import com.sepideh.lilo.core.presentation.UiText
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.model.Category

data class TaskListState(
    val searchQuery: String = "",
    val tasksResult: List<Task> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategory : Int?= null,
    val selectedTask: Task? = null,
    val titleError : String ? = null,
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null
)


