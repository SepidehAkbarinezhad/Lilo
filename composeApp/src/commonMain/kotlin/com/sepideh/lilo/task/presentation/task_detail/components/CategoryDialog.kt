package com.sepideh.lilo.task.presentation.task_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailEvent
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailState
import com.sepideh.lilo.task.presentation.task_list.TaskListEvent
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.category_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun CategoryDialog(state: TaskDetailState, onEvent: (BaseEvent) -> Unit) {
    AppDialog(dialogModel = DialogModel(content = {
        CategoryDialogHeader(onEvent = onEvent)
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            state.categories.forEachIndexed { index, category ->
                AppText(modifier = Modifier.padding(vertical = 4.dp),text = category.title, textType = TextType.SubTitle, color = MaterialTheme.colorScheme.primary)
                if (index != state.categories.lastIndex) {
                    Spacer(
                        modifier = Modifier.fillMaxWidth().height(1.dp)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }

            }
        }
    }, onDismissRequest = { onEvent(TaskDetailEvent.OnDismissCategoryDialog) }))
}

@Composable
fun CategoryDialogHeader(onEvent: (BaseEvent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.width(48.dp)) // placeholder for symmetry (if needed)

        AppText(
            text = stringResource(Res.string.category_label),
            textType = TextType.Title,
            color = MaterialTheme.colorScheme.tertiary
        )

        IconButton(onClick = { onEvent(TaskListEvent.OnFilterIcon) }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}
