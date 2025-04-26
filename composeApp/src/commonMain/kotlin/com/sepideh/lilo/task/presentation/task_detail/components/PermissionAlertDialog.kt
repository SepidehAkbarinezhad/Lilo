package com.sepideh.lilo.task.presentation.task_detail.components


import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailEvent
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.cancel_button
import lilo.composeapp.generated.resources.confirm_button
import lilo.composeapp.generated.resources.permission_alert_dialog_1
import lilo.composeapp.generated.resources.permission_alert_dialog_2

@Composable
fun PermissionAlertDialog(state: TaskDetailState, onEvent: (BaseEvent) -> Unit) {
    AppDialog(dialogModel = DialogModel(content = {
        Column {
            AppText(text = Res.string.permission_alert_dialog_1)
            AppText(text = Res.string.permission_alert_dialog_2)
            AppRowButtons(firstButtonTitle = Res.string.confirm_button, onFirstButtonClick = {onEvent(TaskDetailEvent.OnGoSettingButton)},
                secondButtonTitle = Res.string.cancel_button, onSecondButtonClick = {onEvent(TaskDetailEvent.OnCancelPermissionDialogButton)})
        }

    }, onDismissRequest = { onEvent(TaskDetailEvent.OnDismissPriorityDialog) }))
}

