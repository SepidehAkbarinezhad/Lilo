package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppOutlineTextField
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.TextFieldRequired
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.reminder.ReminderPicker
import com.sepideh.lilo.task.presentation.reminder.TimePickerContainer
import com.sepideh.lilo.task.presentation.task_detail.components.CategoryDialog
import com.sepideh.lilo.task.presentation.task_detail.components.PermissionAlertDialog
import com.sepideh.lilo.task.presentation.task_detail.components.PermissionDeniedDialog
import com.sepideh.lilo.task.presentation.task_detail.components.PriorityDialog
import com.sepideh.lilo.task.presentation.task_detail.components.TaskDetailIcons
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.add_task_label
import lilo.composeapp.generated.resources.add_task_title
import lilo.composeapp.generated.resources.cancel_button
import lilo.composeapp.generated.resources.description_label
import lilo.composeapp.generated.resources.edit_task_label
import lilo.composeapp.generated.resources.edit_task_title
import lilo.composeapp.generated.resources.title_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskDetailScreenRoot(
    taskId: Long?,
    viewModel: TaskDetailViewModel,
    onNavigateTo: (AppDestinations) -> Unit
) {
    val state by viewModel.stateValue.collectAsStateWithLifecycle()
    val task = viewModel.task

    LaunchedEffect(taskId) {
        taskId?.let {
            viewModel.onEvent(TaskDetailEvent.OnGetSelectedTaskInfo(it))
        }
    }

    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        bodyContainer = {
            if(!state.isReminderDialogOpen){
                TaskDetailScreen(
                    state = state,
                    task = task,
                    onEvent = viewModel::onEvent
                )
            }

        },
        dialogContent = {
            if (state.isCategoryDialogOpen) {
                CategoryDialog(state = state, onEvent = { viewModel.onEvent(it) })
            }
            if (state.isPriorityDialogOpen) {
                PriorityDialog(state = state, onEvent = { viewModel.onEvent(it) })
            }
            if (state.isTimeDialogOpen) {
                TimePickerContainer(
                    reminderModel = viewModel.reminderModel, onConfirm = {
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
            if (state.isReminderDialogOpen) {
                println("if (state.isDateDialogOpen)")
                ReminderPicker( reminderModel = viewModel.reminderModel, onEvent = {viewModel.onEvent(it)})
            }
            if (state.shouldShowPermissionDialog) {
                PermissionAlertDialog(state = state, onEvent = { viewModel.onEvent(it) })
            }
            if (state.shouldShowPermissionDeniedDialog) {
                PermissionDeniedDialog(state = state, onEvent = { viewModel.onEvent(it) })
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
    val isEdit = task.id != null
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
                .statusBarsPadding(),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                AppText(
                    modifier = Modifier.padding(vertical = 18.dp),
                    text = when (isEdit) {
                        true -> Res.string.edit_task_title
                        else -> Res.string.add_task_title
                    }, textType = TextType.Title, color = Color.White
                )
            }
            Surface(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    AppOutlineTextField(
                        containerModifier = Modifier.padding(18.dp),
                        textFieldRequired = TextFieldRequired(
                            value = task.title,
                            onValueChange = { onEvent(TaskDetailEvent.OnTitleChanged(it)) },
                            label = stringResource(Res.string.title_label),
                            validationStatus = state.titleError
                        )
                    )


                    AppOutlineTextField(
                        containerModifier = Modifier.padding(18.dp),
                        textFieldModifier = Modifier.heightIn(116.dp),
                        textFieldRequired = TextFieldRequired(
                            value = task.description,
                            onValueChange = { onEvent(TaskDetailEvent.OnDescriptionChanged(it)) },
                            label = stringResource(Res.string.description_label),
                            validationStatus = state.descriptionError
                        ),
                        singleLine = false,
                        maxLines = 3,
                    )

                    TaskDetailIcons(onEvent = onEvent)
                }
            }

        }
        AppRowButtons(
            modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp),
            firstButtonTitle = when (isEdit) {
                true -> Res.string.edit_task_label
                else -> Res.string.add_task_label
            },
            onFirstButtonClick = {
                onEvent(TaskDetailEvent.OnAddTaskButton(checkPermission = true))
            },
            secondButtonTitle = Res.string.cancel_button,
            onSecondButtonClick = { onEvent(BaseEvent.OnNavigateTo(AppDestinations.NavigateUp())) }
        )


    }

}