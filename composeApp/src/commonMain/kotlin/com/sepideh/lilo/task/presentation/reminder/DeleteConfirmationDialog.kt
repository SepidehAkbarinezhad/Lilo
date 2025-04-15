package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.cancel_button
import lilo.composeapp.generated.resources.confirm_button
import lilo.composeapp.generated.resources.delete_task_confirmation

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AppDialog(
        dialogModel = DialogModel(content = {
                AppText(textType = TextType.SubTitle, text = Res.string.delete_task_confirmation)
                Box(modifier = Modifier.height(8.dp))
                AppRowButtons(
                    firstButtonTitle = Res.string.confirm_button,
                    onFirstButtonClick = onConfirm,
                    secondButtonTitle = Res.string.cancel_button,
                    onSecondButtonClick = onDismiss
                )
        }, onDismissRequest = onDismiss),
    )
}