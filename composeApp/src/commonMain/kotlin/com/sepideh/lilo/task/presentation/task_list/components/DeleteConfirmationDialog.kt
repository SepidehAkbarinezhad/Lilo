package com.sepideh.lilo.task.presentation.task_list.components

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
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.cancel_button
import lilo.composeapp.generated.resources.confirm_button
import lilo.composeapp.generated.resources.delete_task_confirmation
import lilo.composeapp.generated.resources.delete_task_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AppDialog(
        dialogModel = DialogModel(content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.heightIn(max = 100.dp),
                    painter = painterResource(Res.drawable.delete_task_logo),
                    contentDescription = ""
                )
                AppText(text = Res.string.delete_task_confirmation,textType = TextType.SubTitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                AppRowButtons(
                    firstButtonTitle = Res.string.confirm_button,
                    onFirstButtonClick = onConfirm,
                    secondButtonTitle = Res.string.cancel_button,
                    onSecondButtonClick = onDismiss,
                )
            }
        }, onDismissRequest = onDismiss),
    )
}