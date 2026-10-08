package com.sepideh.lilo.task.presentation.detail

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.task.presentation.TaskGroupUi
import org.jetbrains.compose.resources.StringResource

sealed interface TaskDetailAction : BaseAction {
    data class OnGroupDraftSelected(val id: Long?) : TaskDetailAction
    data object OnManageGroups : TaskDetailAction
    data object OnCloseManageGroups : TaskDetailAction
    data object OnConfirmGroup : TaskDetailAction
    data object OnDismissReminder : TaskDetailAction
    data class OnReminderConfirmed(val at: Long, val repeat: com.sepideh.lilo.task.domain.reminder.RepeatRule, val zoneId: String) : TaskDetailAction
    data class OnImagesSelected(val sources: List<com.sepideh.lilo.core.domain.images.ImageSource>) : TaskDetailAction
    data object OnClearImages : TaskDetailAction
    data object OnImagePickerFailure : TaskDetailAction
    data object OnClearReminder : TaskDetailAction
    data class OnTitleChanged(val title: String) : TaskDetailAction
    data class OnDescriptionChanged(val description: String) : TaskDetailAction
    data object OnGroupIcon : TaskDetailAction
    data object OnDismissGroupDialog : TaskDetailAction
    data object OnDateReminderIcon : TaskDetailAction
    data class OnGroupSelected(val group: TaskGroupUi) : TaskDetailAction
    data class OnPriorityIdSelected(val id: Int) : TaskDetailAction
    data class OnPrioritySelected(val title: StringResource) : TaskDetailAction
    // Constructor retained for deferred note code; saving always validates reminder access.
    data class OnAddTaskButton(val checkDeniedPermission: Boolean = true) : TaskDetailAction
    data class OnAddNewGroup(val groupTitle: String) : TaskDetailAction
    data class OnRenameGroup(val groupId: Long, val title: String) : TaskDetailAction
    data class OnDeleteGroup(val groupId: Long) : TaskDetailAction
    data class OnGetSelectedTaskInfo(val taskId: Long) : TaskDetailAction
    data class OnGrantPermissionButton(val firstTime: Boolean = false) : TaskDetailAction
    data object OnAskSaveWithoutReminder : TaskDetailAction
    data object OnSaveWithoutReminder : TaskDetailAction
    data object OnPermissionReturn : TaskDetailAction
    data object OnCancelPermissionDialog : TaskDetailAction

}
