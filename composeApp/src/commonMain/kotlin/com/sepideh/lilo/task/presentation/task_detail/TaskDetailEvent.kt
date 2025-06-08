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
    data object OnDateReminderIcon : TaskDetailEvent
    data object OnDismissReminderDialogButton : TaskDetailEvent
    data object OnDismissTimeDialog : TaskDetailEvent
    data class OnCategorySelected(val title: String) : TaskDetailEvent
    data class OnPrioritySelected(val title: String) : TaskDetailEvent
    data class OnSelectReminderDate(val date: Pair<Long?, Long?>) : TaskDetailEvent
    data class OnSelectReminderTime(val time: Pair<Int?, Int?>) : TaskDetailEvent
    data class OnSelectReminderConfirm(val reminderModel: ReminderModel) : TaskDetailEvent
    data class OnAddTaskButton(val checkPermission : Boolean = false) : TaskDetailEvent
    data class OnAddNewCategory(val category: Category) : TaskDetailEvent
    data class OnGetSelectedTaskInfo(val taskId: Long) : TaskDetailEvent
    data class OnGrantPermissionButton(val firstTime : Boolean) : TaskDetailEvent
    data object OnCancelPermissionDialog : TaskDetailEvent

}