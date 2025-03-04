package com.sepideh.lilo.task.presentation.task_list

import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.task.domain.Task


sealed interface TaskListEvent : BaseEvent {
    data class OnTabSelected(val index: Int) : TaskListEvent
    data class OnSearchQueryChange(val query: String) : TaskListEvent
    data object OnAddNewTaskClick : TaskListEvent
    data object DismissContact : TaskListEvent
    data class OnTitleChanged(val value: String) : TaskListEvent
    data class OnDescriptionChanged(val value: String) : TaskListEvent
    class OnPhotoPicked(val bytes: ByteArray) : TaskListEvent
    data object OnAddPhotoClicked : TaskListEvent
    data object SaveTask : TaskListEvent
    data class OnEditTask(val task: Task) : TaskListEvent
    data class OnDeleteTask(val task: Task) : TaskListEvent
}