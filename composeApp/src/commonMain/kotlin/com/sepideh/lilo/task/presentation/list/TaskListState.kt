package com.sepideh.lilo.task.presentation.list

import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.TaskGroupUi
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
    val groups: List<TaskGroupUi> = emptyList(),
    val isDeleteDialogOpen: Boolean = false,
    val selectedGroup: Long? = null,
    val selectedTask: Task? = null,
    val titleError: String? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
)


