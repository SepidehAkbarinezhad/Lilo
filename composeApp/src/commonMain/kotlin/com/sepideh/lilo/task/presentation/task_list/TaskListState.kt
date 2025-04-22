package com.sepideh.lilo.task.presentation.task_list

import com.sepideh.lilo.core.presentation.UiText
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.model.Category
import com.sepideh.lilo.task.presentation.model.TaskFilterOption

data class TaskListState(
    val searchQuery: String = "",
    val isFilterSheetOpen: Boolean = false,
    val taskFilterOption: TaskFilterOption = TaskFilterOption(),
    val tempFilterOption: TaskFilterOption = TaskFilterOption(),
    val tasksResult: List<Task> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isDeleteDialogOpen :Boolean = false,
    val selectedCategory : Long?= null,
    val selectedTask: Task? = null,
    val titleError : String ? = null,
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null
)


