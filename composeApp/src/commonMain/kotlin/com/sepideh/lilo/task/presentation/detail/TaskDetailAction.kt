package com.sepideh.lilo.task.presentation.detail

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.category.presentation.CategoryPresentation
import org.jetbrains.compose.resources.StringResource

sealed interface TaskDetailAction : BaseAction {
    data class OnGroupDraftSelected(val id: Long?) : TaskDetailAction
    data object OnManageGroups : TaskDetailAction
    data object OnCloseManageGroups : TaskDetailAction
    data object OnConfirmGroup : TaskDetailAction
    data object OnDismissReminder : TaskDetailAction
    data class OnReminderConfirmed(val at: Long, val repeat: com.sepideh.lilo.task.domain.reminder.RepeatRule, val zoneId: String) : TaskDetailAction
    data object OnClearReminder : TaskDetailAction
    data class OnTitleChanged(val title: String) : TaskDetailAction
    data class OnDescriptionChanged(val description: String) : TaskDetailAction
    data object OnCategoryIcon : TaskDetailAction
    data object OnDismissCategoryDialog : TaskDetailAction
    data object OnPriorityIcon : TaskDetailAction
    data object OnDismissPriorityDialog : TaskDetailAction
    data object OnDateReminderIcon : TaskDetailAction
    data class OnCategorySelected(val category: CategoryPresentation) : TaskDetailAction
    data class OnPrioritySelected(val title: StringResource) : TaskDetailAction
    // Constructor retained for deferred note code; saving always validates reminder access.
    data class OnAddTaskButton(val checkDeniedPermission: Boolean = true) : TaskDetailAction
    data class OnAddNewCategory(val categoryTitle: String) : TaskDetailAction
    data class OnDeleteCategory(val categoryId: Long) : TaskDetailAction
    data class OnGetSelectedTaskInfo(val taskId: Long) : TaskDetailAction
    data class OnGrantPermissionButton(val firstTime: Boolean = false) : TaskDetailAction
    data object OnAskSaveWithoutReminder : TaskDetailAction
    data object OnSaveWithoutReminder : TaskDetailAction
    data object OnPermissionReturn : TaskDetailAction
    data object OnCancelPermissionDialog : TaskDetailAction

}
