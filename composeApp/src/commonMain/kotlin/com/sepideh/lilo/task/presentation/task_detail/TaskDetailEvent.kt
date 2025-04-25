package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.task.presentation.model.Category

sealed interface TaskDetailEvent : BaseEvent {
    data class OnTitleChanged(val title: String) : TaskDetailEvent
    data class OnDescriptionChanged(val description: String) : TaskDetailEvent
    data object OnCategoryIcon : TaskDetailEvent
    data object OnDismissCategoryDialog : TaskDetailEvent
    data object OnPriorityIcon : TaskDetailEvent
    data object OnDismissPriorityDialog : TaskDetailEvent
    data object OnDateIcon : TaskDetailEvent
    data object OnDismissDateDialog : TaskDetailEvent
    data object OnTimeIcon : TaskDetailEvent
    data object OnDismissTimeDialog : TaskDetailEvent
    data class OnCategorySelected(val title: String) : TaskDetailEvent
    data class OnPrioritySelected(val title: String) : TaskDetailEvent
    data class OnSelectReminderDate(val date: Pair<Long?, Long?>) : TaskDetailEvent
    data class OnSelectReminderTime(val time: Pair<Int?, Int?>) : TaskDetailEvent
    data object OnAddTaskButton : TaskDetailEvent
    data class OnAddNewCategory(val category: Category) : TaskDetailEvent
    data class OnGetSelectedTaskInfo(val taskId: Long) : TaskDetailEvent
    data class OnShowPermissionDialog(val hasAlarm : Boolean,val hasNotification: Boolean) : TaskDetailEvent

}