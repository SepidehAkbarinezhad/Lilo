package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.runtime.Composable
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseRoot

@Composable
fun TaskDetailScreenRoot(
    viewModel: TaskDetailViewModel,
    onNavigateTo: (AppDestinations) -> Unit
) {
    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        bodyContainer = { TaskDetailScreen(onEvent = viewModel::onEvent) })

}

@Composable
fun TaskDetailScreen(
    onEvent: (BaseEvent) -> Unit
) {
}