package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.sepideh.lilo.core.presentation.components.AppDropDown
import com.sepideh.lilo.core.presentation.components.AppOutlineTextField
import com.sepideh.lilo.core.presentation.components.AppSingleButton
import com.sepideh.lilo.core.presentation.components.TextFieldRequired
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.reminder.DateRangePickerModal
import com.sepideh.lilo.task.presentation.reminder.TimePickerContainer
import com.sepideh.lilo.task.presentation.task_detail.components.CategoryDialog
import com.sepideh.lilo.task.presentation.task_detail.components.TaskDetailIcons
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.add_task
import lilo.composeapp.generated.resources.description_label
import lilo.composeapp.generated.resources.title_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskDetailScreenRoot(
    viewModel: TaskDetailViewModel,
    onNavigateTo: (AppDestinations) -> Unit
) {

    val state by viewModel.stateValue.collectAsStateWithLifecycle()
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
        },
        dialogContent = {
            if (state.isCategoryDialogOpen) {
                CategoryDialog(state = state, onEvent = { viewModel.onEvent(it) })
            }
            if (state.isPriorityDialogOpen) {
            }
            if (state.isTimeDialogOpen) {
                TimePickerContainer(onConfirm = {
                    viewModel.onEvent(
                        TaskDetailEvent.OnSelectReminderTime(it)
                    )
                    viewModel.onEvent(TaskDetailEvent.OnDismissTimeDialog)
                }, onDismiss = {
                    viewModel.onEvent(TaskDetailEvent.OnDismissTimeDialog)
                    viewModel.onEvent(
                        TaskDetailEvent.OnSelectReminderTime(Pair(null, null))
                    )
                })
            }
            if (state.isDateDialogOpen) {
                DateRangePickerModal(onDateRangeSelected = { pair ->
                    println("start: ${pair.first}  end: ${pair.second}")
                    viewModel.onEvent(TaskDetailEvent.OnSelectReminderDate(pair))
                    viewModel.onEvent(TaskDetailEvent.OnDismissDateDialog)
                }, onDismiss = { viewModel.onEvent(TaskDetailEvent.OnDismissDateDialog) })
            }
        }
    )

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
                        label = stringResource(Res.string.title_label),
                        validationStatus = state.titleError
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
                TaskDetailIcons(onEvent = onEvent)
            }
        }

        AppSingleButton(
            text = Res.string.add_task,
            onClick = {
                onEvent(TaskDetailEvent.OnAddTaskButton)
                onEvent(BaseEvent.OnNavigateTo(AppDestinations.NavigateUp()))
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }

}