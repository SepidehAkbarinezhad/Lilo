package com.sepideh.lilo.task.presentation.task_list

import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.task.domain.Task
import com.sepideh.lilo.task.presentation.model.Category


sealed interface TaskListEvent : BaseEvent {
    data class OnTabSelected(val index: Int) : TaskListEvent
    data class OnSearchQueryChange(val query: String) : TaskListEvent
    data object OnAddNewTaskClick : TaskListEvent
    data class OnEditTask(val task: Task) : TaskListEvent
    data class OnDeleteTask(val task: Task) : TaskListEvent
    data class OnTitleChanged(val title: String) : TaskListEvent
    data class OnDescriptionChanged(val value: String) : TaskListEvent
    data class OnDoneChange(val task : Task) : TaskListEvent
    class OnPhotoPicked(val bytes: ByteArray) : TaskListEvent
}