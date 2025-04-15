package com.sepideh.lilo.task.presentation.task_detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.task.presentation.task_list.TaskListEvent
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.category_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun CategoryDialog(onEvent: (BaseEvent) -> Unit) {
    AppDialog(dialogModel = DialogModel(content = {
        CategoryDialogHeader(onEvent = onEvent)
    }, onDismissRequest = {}))
}

@Composable
fun CategoryDialogHeader(onEvent: (BaseEvent) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        AppText(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(Res.string.category_label),
            textType = TextType.Title,
            color = MaterialTheme.colorScheme.tertiary
        )
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { onEvent(TaskListEvent.OnFilterIcon) }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }

}