package com.sepideh.lilo.task.presentation.task_detail.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailEvent
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.add_task_label
import lilo.composeapp.generated.resources.alert_icon
import lilo.composeapp.generated.resources.grant_permission_button
import lilo.composeapp.generated.resources.permission_alert_dialog_denied
import org.jetbrains.compose.resources.painterResource

@Composable
fun PermissionDeniedDialog(state: TaskDetailState, onEvent: (BaseEvent) -> Unit) {
    AppDialog(dialogModel = DialogModel(content = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.heightIn(max = 100.dp),
                painter = painterResource(Res.drawable.alert_icon),
                contentDescription = ""
            )
            AppText(text = Res.string.permission_alert_dialog_denied,textType = TextType.SubTitle,)
            Spacer(modifier = Modifier.height(8.dp))
            AppRowButtons(firstButtonTitle = Res.string.grant_permission_button,
                onFirstButtonClick = { onEvent(TaskDetailEvent.OnGrantPermissionButton) },
                secondButtonTitle = Res.string.add_task_label,
                onSecondButtonClick = { onEvent(TaskDetailEvent.OnAddTaskButton(checkPermission = false)) })
        }
    }, onDismissRequest = { onEvent(TaskDetailEvent.OnDismissPriorityDialog) }))
}

