package com.sepideh.lilo.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sepideh.lilo.task.domain.Task
import com.sepideh.lilo.task.presentation.task_list.components.TaskListItem
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun TaskListItemPreview(modifier: Modifier = Modifier) {
    TaskListItem(task = Task(title = "title", description = "description")) { }
}