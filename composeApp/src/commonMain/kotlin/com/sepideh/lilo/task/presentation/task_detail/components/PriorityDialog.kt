package com.sepideh.lilo.task.presentation.task_detail.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.task.presentation.model.Priority.Companion.priorities
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailAction
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.priority_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun PriorityDialog(state: TaskDetailState, onAction: (BaseAction) -> Unit) {

    val contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
    var selected by remember { mutableStateOf(state.selectedPriority) }
    AppDialog(dialogModel = DialogModel(content = {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {
                AppText(
                    modifier = Modifier.align(Alignment.Center),
                    text = stringResource(Res.string.priority_label),
                    textType = TextType.Title,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            priorities.forEachIndexed { index, priority ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = selected == priority, onCheckedChange = {
                            selected = priority
                        },
                        colors = CheckboxDefaults.colors(
                            uncheckedColor = contentColor
                        )
                    )
                    AppText(
                        modifier = Modifier.padding(vertical = 4.dp),
                        text = priority.title,
                        textType = TextType.SubTitle,
                        color = if (selected == priority) MaterialTheme.colorScheme.primary else contentColor
                    )
                }

                if (index != priorities.lastIndex) {
                    Spacer(
                        modifier = Modifier.fillMaxWidth().height(1.dp)
                            .background(contentColor)
                    )
                }
            }
            Box(Modifier.fillMaxWidth()) {
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = {
                        onAction(TaskDetailAction.OnPrioritySelected(selected.title))
                    }
                ) {
                   /* Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )*/
                }
            }

        }

    }, onDismissRequest = { onAction(TaskDetailAction.OnDismissPriorityDialog) }))
}

