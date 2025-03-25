package com.sepideh.lilo.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sepideh.lilo.task.domain.Task
import com.sepideh.lilo.task.presentation.task_list.TaskListScreen
import com.sepideh.lilo.task.presentation.task_list.TaskListState


private val tasks = (1..100).map {
    Task(id = it.toLong(),title = "title: $it", description = "description: $it")
}
@Preview
@Composable
fun TaskListScreenPreview(modifier: Modifier = Modifier) {
    TaskListScreen(state = TaskListState(tasksResult = tasks), newTask = null) { }
}