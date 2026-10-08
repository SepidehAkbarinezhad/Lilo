package com.sepideh.lilo.task.presentation.list.components

import com.sepideh.lilo.core.presentation.icons.LiloIcons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import com.sepideh.lilo.task.presentation.reminder.taskReminderLabel
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.gesture.SwipeToRevealDelete
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
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
        }, shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .7f)),
            color = MaterialTheme.colorScheme.surface) {
            Row(Modifier.fillMaxWidth().heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically) {
                com.sepideh.lilo.core.presentation.components.LiloCheckbox(checked = task.done, enabled = clickable,
                    label = completionLabel, accent = LiloExtendedTheme.colors.taskColor,
                    modifier = Modifier.padding(start = 8.dp),
                    onCheckedChange = { onAction(TaskListAction.OnDoneChange(task.copy(done = it))) })
                AppText(text = task.title, textType = TextType.BodyLarge, maxLines = 1,
                    textDecoration = if (task.done) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None,
                    modifier = Modifier.weight(1f).padding(end = 12.dp), color = LiloExtendedTheme.colors.textPrimary)
                Column(Modifier.widthIn(max = 150.dp).padding(vertical = 14.dp, horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.End) {
                    if (!groupLabel.isNullOrBlank()) Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .06f)) {
                        AppText(text = groupLabel, textType = TextType.Body, maxLines = 1,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (task.reminderAt != null) Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        AppText(text = taskReminderLabel(task), textType = TextType.Caption, maxLines = 2, modifier = Modifier.weight(1f, fill = false), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Icon(LiloIcons.Bell, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                val priority = Priority.getById(task.priority)
                Icon(org.jetbrains.compose.resources.vectorResource(Res.drawable.lilo_priority_flag),
                    stringResource(priority.title), Modifier.padding(end = 14.dp).size(16.dp), tint = priority.color)
            }
        }
    }
}
@AppPreviews
@Composable
private fun TaskListItemPreview() {
    LiloPreviewWrapper {
        val task = Task(title = "Practice violin", description = "play violin", priority = 1)
        TaskListItem(
            task = task,
            clickable = true,
            onAction = {},
            groupLabel = "learning"
        )
    }
}