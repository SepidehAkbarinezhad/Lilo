package com.sepideh.lilo.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sepideh.lilo.task.presentation.task_list.TaskListScreen
import com.sepideh.lilo.task.presentation.task_list.TaskListState

@Preview
@Composable
fun TaskListScreenPreview(modifier: Modifier = Modifier) {
    TaskListScreen(state = TaskListState()) { }
}