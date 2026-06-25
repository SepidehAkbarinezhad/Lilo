package com.sepideh.lilo.task.presentation.task_list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.model.Priority.Companion.priorities
import com.sepideh.lilo.task.presentation.task_list.TaskListAction
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TaskListItem(
    modifier: Modifier = Modifier,
    clickable: Boolean,
    task: Task,
    onAction: (BaseAction) -> Unit
) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    ) {
        with(task) {
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularCheckbox(
                    checked = done,
                    onCheckedChange = {
                        if (clickable) onAction(
                            TaskListAction.OnDoneChange(
                                task = task.copy(done = !done)
                            )
                        )
                    },
                    modifier = Modifier.padding(start = 16.dp)
                )
                Column(
                    Modifier.weight(.8f).padding(vertical = 8.dp , horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(
                        text = title,
                        textType = TextType.SubTitle,
                        textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (task.done) 0.5f else 1f)
                    )
                    AppText(
                        text = description,
                        textType = TextType.Body,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (task.done) 0.6f else 0.9f)
                    )
                }
                IconButton(onClick = {
                    if (clickable)
                        onAction(TaskListAction.OnDeleteTaskIcon(task = task))
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "delete Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                        .width(4.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(color = priorities[priority].color)
                )
            }
        }
    }
}

@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .size(22.dp)
            .clip(CircleShape)
            .border(width = 2.dp, color = color, shape = CircleShape)
            .clickable(enabled = enabled) { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview
@Composable
fun PendingTaskItemPreview(modifier: Modifier = Modifier) {
    TaskListItem(
        task = Task(id = 0, title = "title", description = "description"),
        clickable = true
    ) { }
}

@Preview
@Composable
fun DoneTaskItemPreview(modifier: Modifier = Modifier) {
    TaskListItem(
        task = Task(id = 0, title = "title", description = "description",done = true),
        clickable = true
    ) { }
}
