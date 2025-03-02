package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.components.AppOutlineTextField
import com.sepideh.lilo.core.presentation.components.TextFieldRequired
import com.sepideh.lilo.task.domain.Task

@Composable
fun TaskDetailScreenRoot(
    viewModel: TaskDetailViewModel,
    onNavigateTo: (AppDestinations) -> Unit
) {
    val task = viewModel.task

    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        bodyContainer = { TaskDetailScreen(task = task, onEvent = viewModel::onEvent) })

}

@Composable
fun TaskDetailScreen(
    task: Task,
    onEvent: (BaseEvent) -> Unit
) {
    LazyColumn {
        item {
            AppOutlineTextField(
                textFieldRequired = TextFieldRequired(
                    value = task.title,
                    onValueChange = { onEvent(TaskDetailEvent.OnTitleChanged(it)) },

                    )
            )
        }
        item {
            AppOutlineTextField(
                textFieldRequired = TextFieldRequired(
                    value = task.description,
                    onValueChange = { onEvent(TaskDetailEvent.OnDescriptionChanged(it)) },

                    )
            )
        }


    }
}