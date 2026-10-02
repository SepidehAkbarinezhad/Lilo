package com.sepideh.lilo.task.presentation.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.gesture.SwipeToRevealDelete
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.list.TaskListAction
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskListItem(modifier: Modifier = Modifier, clickable: Boolean, task: Task, onAction: (BaseAction) -> Unit, groupLabel: String? = null) {
    val deleteLabel = stringResource(Res.string.delete_action)
    val completionLabel = stringResource(if (task.done) Res.string.task_reopen_action else Res.string.task_complete_action)
    SwipeToRevealDelete(enabled = clickable, onDelete = { onAction(TaskListAction.OnDeleteTaskIcon(task)) }) {
        Surface(modifier = modifier.semantics {
            customActions = if (!clickable) emptyList() else listOf(CustomAccessibilityAction(deleteLabel) { onAction(TaskListAction.OnDeleteTaskIcon(task)); true })
        }, shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .6f)),
            color = MaterialTheme.colorScheme.surface) {
            Row(Modifier.fillMaxWidth().heightIn(min = 76.dp).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.width(3.dp).height(32.dp).background(Priority.getById(task.priority).color))
                Checkbox(checked = task.done, onCheckedChange = { onAction(TaskListAction.OnDoneChange(task.copy(done = it))) }, enabled = clickable,
                    modifier = Modifier.semantics { contentDescription = completionLabel },
                    colors = CheckboxDefaults.colors(checkedColor = LiloExtendedTheme.colors.taskColor, checkmarkColor = MaterialTheme.colorScheme.onSurface))
                Column(Modifier.weight(1f).padding(end = 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AppText(text = task.title, textType = TextType.BodyLarge, maxLines = 2,
                        textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (task.done) .5f else 1f))
                    if (task.description.isNotBlank()) AppText(text = task.description, textType = TextType.Caption, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (!groupLabel.isNullOrBlank()) {
                        Surface(shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .06f)) {
                            AppText(text = groupLabel, textType = TextType.Caption, maxLines = 1,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
