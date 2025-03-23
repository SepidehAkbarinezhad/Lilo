package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.components.AppButton
import com.sepideh.lilo.core.presentation.components.AppDropDown
import com.sepideh.lilo.core.presentation.components.AppOutlineTextField
import com.sepideh.lilo.core.presentation.components.TextFieldRequired
import com.sepideh.lilo.task.domain.Task
import com.sepideh.lilo.task.presentation.model.Category.Companion.categories
import com.sepideh.lilo.task.presentation.model.Priority.Companion.priorities
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.add_task
import lilo.composeapp.generated.resources.category_label
import lilo.composeapp.generated.resources.description_label
import lilo.composeapp.generated.resources.priority_label
import lilo.composeapp.generated.resources.title_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskDetailScreenRoot(
    viewModel: TaskDetailViewModel,
    onNavigateTo: (AppDestinations) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val task = viewModel.task

    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        bodyContainer = {
            TaskDetailScreen(
                state = state,
                task = task,
                onEvent = viewModel::onEvent
            )
        })

}

@Composable
fun TaskDetailScreen(
    state: TaskDetailState,
    task: Task,
    onEvent: (BaseEvent) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                AppOutlineTextField(
                    textFieldRequired = TextFieldRequired(
                        value = task.title,
                        onValueChange = { onEvent(TaskDetailEvent.OnTitleChanged(it)) },
                        label = stringResource(Res.string.title_label)
                    )
                )
            }
            item {
                AppOutlineTextField(
                    textFieldRequired = TextFieldRequired(
                        value = task.description,
                        onValueChange = { onEvent(TaskDetailEvent.OnDescriptionChanged(it)) },
                        label = stringResource(Res.string.description_label),
                    ),
                    singleLine = false
                )
            }
            item {
                AppDropDown(
                    selectedValue = state.selectedCategory?.title?: categories[0].title,
                    options = state.categories.map { it.title },
                    label = stringResource(Res.string.category_label),
                    onValueChanged = {
                        println("onValueChanged")
                        onEvent(TaskDetailEvent.OnSelectedCategoryChanged(it))})
            }
            item {
                AppDropDown(
                    selectedValue = priorities[0].title ,
                    options = state.priorities.map { it.title },
                    label = stringResource(Res.string.priority_label),
                    onValueChanged = {onEvent(TaskDetailEvent.OnSelectedPriorityChanged(it))})
            }

        }
        AppButton(
            text = Res.string.add_task,
            onClick = {
                onEvent(TaskDetailEvent.OnAddTask)
                onEvent(BaseEvent.OnNavigateTo(AppDestinations.TaskList()))
            },
            modifier = Modifier.fillMaxWidth().padding(24.dp).align(Alignment.BottomCenter)
        )

    }

}