package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppOutlineTextField
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.TextFieldRequired
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.reminder.ReminderPicker
import com.sepideh.lilo.task.presentation.task_detail.components.CategoryDialog
import com.sepideh.lilo.task.presentation.task_detail.components.PermissionAlertDialog
import com.sepideh.lilo.task.presentation.task_detail.components.PermissionDeniedDialog
import com.sepideh.lilo.task.presentation.task_detail.components.PriorityDialog
import com.sepideh.lilo.task.presentation.task_detail.components.TaskDetailIcons
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
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
    onNavigateTo: (AppRoutes) -> Unit,
    onBack: () -> Boolean
) {
    val state by viewModel.stateValue.collectAsStateWithLifecycle()
    val task = viewModel.task

    LaunchedEffect(taskId) {
        taskId?.let {
            viewModel.onAction(TaskDetailAction.OnGetSelectedTaskInfo(it))
        }
    }

    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        onBack = onBack,
        bodyContainer = {
            if (!state.isReminderDialogOpen) {
                TaskDetailScreen(
                    state = state,
                    task = task,
                    onAction = viewModel::onAction
                )
            }

        },
        dialogContent = {
            if (state.isCategoryDialogOpen) {
                CategoryDialog(state = state, onAction = { viewModel.onAction(it) })
            }
            if (state.isPriorityDialogOpen) {
                PriorityDialog(state = state, onAction = { viewModel.onAction(it) })
            }
            if (state.isReminderDialogOpen) {
                ReminderPicker(
                    reminderModel = viewModel.reminderModel,
                    onAction = { viewModel.onAction(it) })
            }
            if (state.shouldShowPermissionDialog) {
                PermissionAlertDialog(
                    isXiaomi = viewModel.isXiaomi,
                    onAction = { viewModel.onAction(it) })
            }
            if (state.shouldShowPermissionDeniedDialog) {
                PermissionDeniedDialog(state = state, onAction = { viewModel.onAction(it) })
            }

        }
    )

}

@Composable
fun TaskDetailScreen(
    state: TaskDetailState,
    task: Task,
    onAction: (BaseAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val isEdit = task.id != null
    Box(modifier = Modifier.fillMaxSize().clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) {
        keyboardController?.hide()
    }) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AppText(
                    modifier = Modifier.statusBarsPadding().padding(18.dp),
                    text = when (isEdit) {
                        true -> Res.string.edit_task_title
                        else -> Res.string.add_task_title
                    }, textType = TextType.Title, color = MaterialTheme.colorScheme.onPrimary
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
                            onValueChange = { onAction(TaskDetailAction.OnTitleChanged(it)) },
                            label = stringResource(Res.string.title_label),
                            validationStatus = state.titleError
                        )
                    )


                    AppOutlineTextField(
                        containerModifier = Modifier.padding(18.dp),
                        textFieldModifier = Modifier.heightIn(116.dp),
                        textFieldRequired = TextFieldRequired(
                            value = task.description,
                            onValueChange = { onAction(TaskDetailAction.OnDescriptionChanged(it)) },
                            label = stringResource(Res.string.description_label),
                            validationStatus = state.descriptionError
                        ),
                        singleLine = false,
                        maxLines = 3,
                    )

                    TaskDetailIcons(onAction = onAction)
                }
            }

        }
        AppRowButtons(
            modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding(),
            firstButtonTitle = when (isEdit) {
                true -> Res.string.edit_task_label
                else -> Res.string.add_task_label
            },
            onFirstButtonClick = {
                onAction(TaskDetailAction.OnAddTaskButton(checkDeniedPermission = true))
            },
            secondButtonTitle = Res.string.cancel_button,
            onSecondButtonClick = { onAction(BaseAction.OnNavigateTo(route = null)) }
        )


    }

}