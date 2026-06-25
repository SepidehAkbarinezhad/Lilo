package com.sepideh.lilo.task.presentation.task_list

import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.category.presentation.CategoryPresentation
import com.sepideh.lilo.task.presentation.model.SortOrder
import com.sepideh.lilo.task.presentation.model.TaskFilterOption

data class TaskListState(
    val sortOrder: SortOrder = SortOrder.Priority,
    val isSearchVisible: Boolean = false,
    val searchQuery: String = "",
    val isFilterSheetOpen: Boolean = false,
    val taskFilterOption: TaskFilterOption = TaskFilterOption(),
    val tempFilterOption: TaskFilterOption = TaskFilterOption(),
    val tasksResult: List<Task> = emptyList(),
    val categories: List<CategoryPresentation> = emptyList(),
    val isDeleteDialogOpen: Boolean = false,
    val selectedCategory: Long? = null,
    val selectedTask: Task? = null,
    val titleError: String? = null,
    val isLoading: Boolean = false,
)


