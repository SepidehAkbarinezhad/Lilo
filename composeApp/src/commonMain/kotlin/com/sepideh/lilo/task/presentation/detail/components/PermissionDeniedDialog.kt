package com.sepideh.lilo.task.presentation.detail.components

import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.FeaturePermissionDialog
import com.sepideh.lilo.task.domain.reminder.ReminderPermission
import com.sepideh.lilo.task.presentation.detail.TaskDetailAction
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PermissionDeniedDialog(permission: ReminderPermission, onAction: (BaseAction) -> Unit) {
    val notification = permission == ReminderPermission.NOTIFICATIONS
    FeaturePermissionDialog(
        title = stringResource(if (notification) Res.string.notification_permission_title else Res.string.alarm_permission_title),
        message = stringResource(if (notification) Res.string.notification_permission_body else Res.string.alarm_permission_body),
        accent = LiloExtendedTheme.colors.taskColor,
        onSettings = { onAction(TaskDetailAction.OnGrantPermissionButton()) },
        onCancel = { onAction(TaskDetailAction.OnAskSaveWithoutReminder) },
        onDismiss = { onAction(TaskDetailAction.OnCancelPermissionDialog) },
    )
}
