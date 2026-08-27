package com.sepideh.lilo.task.presentation.task_list

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.task.presentation.model.Enums
import com.sepideh.lilo.task.presentation.model.SortOrder


sealed interface TaskListAction : BaseAction {
    data class OnSortOrderChanged(val sortOrder: SortOrder) : TaskListAction
    data class OnCategorySelected(val id: Long?) : TaskListAction
    data class OnSearchToggle(val open: Boolean) : TaskListAction
    data class OnSearchQueryChange(val query: String) : TaskListAction
    data object OnFilterIcon : TaskListAction
    data object OnCloseFilterIcon : TaskListAction
    data object OnApplyFilter : TaskListAction
    data class OnStatusFilterChanged(val status: Enums) : TaskListAction
    data class OnPriorityFilterChanged(val priority: Priority) : TaskListAction
    data object OnResetFilter : TaskListAction
    data class OnDeleteTaskIcon(val task: Task?) : TaskListAction
    data object OnDismissDeleteDialog : TaskListAction
    data object OnDeleteTaskConfirm : TaskListAction
    data class OnTitleChanged(val title: String) : TaskListAction
    data class OnDescriptionChanged(val value: String) : TaskListAction
    data class OnDoneChange(val task : Task) : TaskListAction
}