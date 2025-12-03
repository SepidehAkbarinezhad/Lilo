package com.sepideh.lilo.task.presentation.task_detail

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.category.domain.model.Category
import com.sepideh.lilo.task.presentation.reminder.ReminderModel
import org.jetbrains.compose.resources.StringResource

sealed interface TaskDetailAction : BaseAction {
    data class OnTitleChanged(val title: String) : TaskDetailAction
    data class OnDescriptionChanged(val description: String) : TaskDetailAction
    data object OnCategoryIcon : TaskDetailAction
    data object OnDismissCategoryDialog : TaskDetailAction
    data object OnPriorityIcon : TaskDetailAction
    data object OnDismissPriorityDialog : TaskDetailAction
    data object OnDateReminderIcon : TaskDetailAction
    data object OnDismissDatePickerButton : TaskDetailAction
    data object OnDismissTimePickerButton : TaskDetailAction
    data class OnCategorySelected(val category: Category) : TaskDetailAction
    data class OnPrioritySelected(val title: StringResource) : TaskDetailAction
    data class OnSelectReminderTime(val time: Pair<Int?, Int?>) : TaskDetailAction
    data class OnReminderDateConfirm(val reminderModel: ReminderModel) : TaskDetailAction
    data class OnReminderTimeConfirm(val reminderModel: ReminderModel) : TaskDetailAction
    data class OnAddTaskButton(val checkDeniedPermission : Boolean = false) : TaskDetailAction
    data class OnAddNewCategory(val categoryTitle: String) : TaskDetailAction
    data class OnGetSelectedTaskInfo(val taskId: Long) : TaskDetailAction
    data class OnGrantPermissionButton(val firstTime : Boolean) : TaskDetailAction
    data object OnCancelPermissionDialog : TaskDetailAction

}