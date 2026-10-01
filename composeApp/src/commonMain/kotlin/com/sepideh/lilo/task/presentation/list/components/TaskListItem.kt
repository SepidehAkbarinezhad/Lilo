package com.sepideh.lilo.task.presentation.list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.list.TaskListAction
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListItem(modifier: Modifier = Modifier, clickable: Boolean, task: Task, onAction: (BaseAction) -> Unit) {
    val deleteLabel = stringResource(Res.string.delete_action)
    val completionLabel = stringResource(if (task.done) Res.string.task_reopen_action else Res.string.task_complete_action)
    val dismiss = rememberSwipeToDismissBoxState(confirmValueChange = { value ->
        if (value == SwipeToDismissBoxValue.EndToStart && clickable) onAction(TaskListAction.OnDeleteTaskIcon(task))
        false // The row remains until deletion is confirmed and persisted.
    })
    SwipeToDismissBox(state = dismiss, enableDismissFromStartToEnd = false, enableDismissFromEndToStart = clickable,
        backgroundContent = {
            Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.errorContainer).padding(20.dp), contentAlignment = Alignment.CenterEnd) {
                Icon(Icons.Outlined.Delete, deleteLabel, tint = MaterialTheme.colorScheme.onErrorContainer)
            }
        }) {
        Surface(modifier = modifier.semantics {
            customActions = listOf(CustomAccessibilityAction(deleteLabel) { onAction(TaskListAction.OnDeleteTaskIcon(task)); true })
        }, color = MaterialTheme.colorScheme.background) {
            Row(Modifier.fillMaxWidth().heightIn(min = 76.dp).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(3.dp).height(32.dp).background(Priority.getById(task.priority).color))
                Checkbox(checked = task.done, onCheckedChange = { onAction(TaskListAction.OnDoneChange(task.copy(done = it))) }, enabled = clickable,
                    modifier = Modifier.semantics { contentDescription = completionLabel },
                    colors = CheckboxDefaults.colors(checkedColor = LiloExtendedTheme.colors.taskColor, checkmarkColor = MaterialTheme.colorScheme.onSurface))
                Column(Modifier.weight(1f).padding(end = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(task.title, style = MaterialTheme.typography.bodyLarge, maxLines = 2,
                        textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (task.done) .5f else 1f))
                    if (task.description.isNotBlank()) Text(task.description, style = MaterialTheme.typography.bodySmall, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
