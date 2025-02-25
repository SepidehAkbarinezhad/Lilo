package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.runtime.Composable
import com.sepideh.lilo.app.navigation.AppDestinations

@Composable
fun TaskDetailScreenRoot(
    viewModel: TaskDetailViewModel,
    onNavigateTo: (AppDestinations) -> Unit
) {

}

@Composable
fun TaskDetailScreen(
    onEvent: (TaskDetailEvent) -> Unit
) {}