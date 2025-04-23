package com.sepideh.lilo.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.task_list.components.TaskListItem

@Preview
@Composable
fun TaskListItemPreview(modifier: Modifier = Modifier) {
    TaskListItem(task = Task(id=0,title = "title", description = "description"), clickable = true) { }
}