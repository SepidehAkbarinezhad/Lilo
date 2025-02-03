package com.sepideh.lilo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.task.presentation.task_list.TaskListScreenRoot
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    val navHostController = rememberNavController()
    NavigationGraph(navHostController = navHostController)
}