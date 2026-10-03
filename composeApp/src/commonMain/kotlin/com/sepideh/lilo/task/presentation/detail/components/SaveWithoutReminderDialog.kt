package com.sepideh.lilo.task.presentation.detail.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.FeatureConfirmationContent
import com.sepideh.lilo.task.presentation.detail.TaskDetailAction
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun SaveWithoutReminderDialog(onAction: (BaseAction) -> Unit) {
    val dismiss = { onAction(TaskDetailAction.OnCancelPermissionDialog) }
    Dialog(onDismissRequest = dismiss) {
        FeatureConfirmationContent(
            accent = LiloExtendedTheme.colors.taskColor,
            title = stringResource(Res.string.save_without_reminder_title),
            message = "",
            confirmLabel = stringResource(Res.string.confirm_action),
            cancelLabel = stringResource(Res.string.cancel_button),
            onConfirm = { onAction(TaskDetailAction.OnSaveWithoutReminder) },
            onDismiss = dismiss,
        )
    }
}
