package com.sepideh.lilo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sepideh.lilo.task.presentation.task_list.TaskListScreenRoot
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    TaskListScreenRoot(viewModel = remember { TaskListViewModel() }, onTaskClicked = {})
}