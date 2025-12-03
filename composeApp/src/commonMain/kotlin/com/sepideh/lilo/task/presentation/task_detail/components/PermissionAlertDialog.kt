package com.sepideh.lilo.task.presentation.task_detail.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailAction
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.ic_alert
import lilo.composeapp.generated.resources.cancel_button
import lilo.composeapp.generated.resources.confirm_button
import lilo.composeapp.generated.resources.permission_Xiaomi_alert_dialog
import lilo.composeapp.generated.resources.permission_alert_dialog
import org.jetbrains.compose.resources.painterResource

@Composable
fun PermissionAlertDialog(isXiaomi: Boolean, onAction: (BaseAction) -> Unit) {
    val reminderMessage = if (isXiaomi) {
        Res.string.permission_Xiaomi_alert_dialog
    } else {
        Res.string.permission_alert_dialog
    }
    AppDialog(dialogModel = DialogModel(content = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.heightIn(max = 100.dp),
                painter = painterResource(Res.drawable.ic_alert),
                contentDescription = ""
            )
            AppText(text = reminderMessage, textType = TextType.SubTitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            AppRowButtons(firstButtonTitle = Res.string.confirm_button,
                onFirstButtonClick = { onAction(TaskDetailAction.OnGrantPermissionButton(firstTime = true)) },
                secondButtonTitle = Res.string.cancel_button,
                onSecondButtonClick = { onAction(TaskDetailAction.OnCancelPermissionDialog) })
        }

    }, onDismissRequest = { onAction(TaskDetailAction.OnCancelPermissionDialog) }))
}

