package com.sepideh.lilo.task.presentation.task_list

import com.sepideh.lilo.core.presentation.UiText
import com.sepideh.lilo.task.domain.Task

data class TaskListState(
    val searchQuery: String = "",
    val searchResults: List<Task> = tasks,
    val selectedTabIndex: Int = 0,
    val selectedTask: Task? = null,
    val isAddTaskSheetOpen : Boolean = false,
    val isSelectedTaskSheetOpen : Boolean = false,
    val titleError : String ? = null,
    val isLoading: Boolean = false,
    val errorMessage: UiText? = null
)


val tasks = (1..20).map { i ->
    Task(id = i.toString(), title = "title $i", description = "description $i")
}
