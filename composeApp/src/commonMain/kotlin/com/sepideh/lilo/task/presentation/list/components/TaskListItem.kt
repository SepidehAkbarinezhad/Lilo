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
import com.sepideh.lilo.core.presentation.components.ItemMetadata
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
    val hasMetadata = !groupLabel.isNullOrBlank() || task.imageNames.isNotEmpty() || task.reminderAt != null
    val deleteLabel = stringResource(Res.string.delete_action)
    val completionLabel = stringResource(if (task.done) Res.string.task_reopen_action else Res.string.task_complete_action)
    SwipeToRevealDelete(enabled = clickable, onDelete = { onAction(TaskListAction.OnDeleteTaskIcon(task)) }) {
        Surface(modifier = modifier.semantics {
            customActions = if (!clickable) emptyList() else listOf(CustomAccessibilityAction(deleteLabel) { onAction(TaskListAction.OnDeleteTaskIcon(task)); true })
        }, shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .7f)),
            color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.fillMaxWidth().heightIn(min = 80.dp).padding(vertical = 8.dp),
                verticalArrangement = if (hasMetadata) Arrangement.spacedBy(4.dp) else Arrangement.Center) {
                Row(Modifier.fillMaxWidth().padding(start = 8.dp, end = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    com.sepideh.lilo.core.presentation.components.LiloCheckbox(checked = task.done, enabled = clickable,
                        label = completionLabel, accent = LiloExtendedTheme.colors.taskColor,
                        onCheckedChange = { onAction(TaskListAction.OnDoneChange(task.copy(done = it))) })
                    AppText(text = task.title, textType = TextType.BodyLarge, maxLines = 1,
                        textDecoration = if (task.done) androidx.compose.ui.text.style.TextDecoration.LineThrough else androidx.compose.ui.text.style.TextDecoration.None,
                        modifier = Modifier.weight(1f).padding(end = 12.dp), color = LiloExtendedTheme.colors.textPrimary)
                    val priority = Priority.getById(task.priority)
                    Icon(org.jetbrains.compose.resources.vectorResource(Res.drawable.lilo_priority_flag),
                        stringResource(priority.title), Modifier.size(20.dp), tint = priority.color)
                }
                if (hasMetadata) {
                    ItemMetadata(groupLabel, task.imageNames.size,
                        modifier = Modifier.padding(start = 20.dp, end = 14.dp, bottom = 4.dp),
                        trailing = if (task.reminderAt != null) ({
                            Row(Modifier.widthIn(max = 180.dp).padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(LiloIcons.Bell, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                AppText(text = taskReminderLabel(task), textType = TextType.Caption, maxLines = 1,
                                    modifier = Modifier.weight(1f, fill = false), color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }) else null)
                }
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