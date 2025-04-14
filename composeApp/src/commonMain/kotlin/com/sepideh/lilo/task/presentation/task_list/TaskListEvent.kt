package com.sepideh.lilo.task.presentation.task_list

import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.task.presentation.model.TaskFilterOption


sealed interface TaskListEvent : BaseEvent {
    data class OnCategorySelected(val id: Int?) : TaskListEvent
    data class OnSearchQueryChange(val query: String) : TaskListEvent
    data object OnFilterIcon : TaskListEvent
    data object OnApplyFilter : TaskListEvent
    data class OnPriorityChanged(val priority: Priority) : TaskListEvent
    data class OnUpdateTempFilter(val filterOption: TaskFilterOption) : TaskListEvent
    data object OnResetFilter : TaskListEvent
    data object OnAddNewTaskClick : TaskListEvent
    data class OnEditTaskIcon(val task: Task) : TaskListEvent
    data class OnDeleteTaskIcon(val task: Task) : TaskListEvent
    data object OnDeleteTaskConfirm : TaskListEvent
    data class OnTitleChanged(val title: String) : TaskListEvent
    data class OnDescriptionChanged(val value: String) : TaskListEvent
    data class OnDoneChange(val task : Task) : TaskListEvent
    class OnPhotoPicked(val bytes: ByteArray) : TaskListEvent
}