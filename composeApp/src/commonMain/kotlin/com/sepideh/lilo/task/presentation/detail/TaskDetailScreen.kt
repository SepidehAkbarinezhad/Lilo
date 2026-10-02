package com.sepideh.lilo.task.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.BaseFormScreen
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.selection.GroupOption
import com.sepideh.lilo.core.presentation.components.selection.GroupSelectionSheet
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.core.presentation.components.form.FormSelectionRow
import com.sepideh.lilo.core.presentation.components.selection.GroupManagementDialog
import com.sepideh.lilo.task.presentation.detail.components.TaskPrioritySelector
import com.sepideh.lilo.task.presentation.detail.components.PermissionAlertDialog
import com.sepideh.lilo.task.presentation.detail.components.PermissionDeniedDialog
import com.sepideh.lilo.task.presentation.reminder.components.ReminderDatePicker
import com.sepideh.lilo.task.presentation.reminder.components.ReminderTimePicker
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskDetailScreenRoot(taskId: Long?, viewModel: TaskDetailViewModel, onNavigateTo: (AppRoutes) -> Unit, onBack: () -> Boolean) {
    val state by viewModel.stateValue.collectAsStateWithLifecycle()
    LaunchedEffect(taskId) { taskId?.let { viewModel.onAction(TaskDetailAction.OnGetSelectedTaskInfo(it)) } }
    BaseRoot(viewModel = viewModel, navigateTo = onNavigateTo, onBack = onBack,
        bodyContainer = { TaskDetailScreen(state, state.task, viewModel::onAction, onBack) },
        dialogContent = {
            if (state.categoryDialogOpen) GroupSelectionSheet(
                groups = state.categories.map { GroupOption(it.id, it.title) }, selectedId = state.draftCategoryId,
                accent = LiloExtendedTheme.colors.taskColor, isAdding = state.isAddingGroup, addedVersion = state.groupAddedVersion,
                onSelect = { viewModel.onAction(TaskDetailAction.OnGroupDraftSelected(it)) },
                onCreate = { viewModel.onAction(TaskDetailAction.OnAddNewCategory(it)) },
                onManage = { viewModel.onAction(TaskDetailAction.OnManageGroups) },
                onConfirm = { viewModel.onAction(TaskDetailAction.OnConfirmGroup) },
                onDismiss = { viewModel.onAction(TaskDetailAction.OnDismissCategoryDialog) },
            )
            if (state.groupManagementOpen) GroupManagementDialog(
                groups = state.categories.map { GroupOption(it.id, it.title, it.isDeletable) },
                deleteMessage = stringResource(Res.string.delete_group_message),
                errorMessage = if (state.hasError) stringResource(Res.string.task_operation_error) else null,
                onDelete = { viewModel.onAction(TaskDetailAction.OnDeleteCategory(it)) },
                onDismiss = { viewModel.onAction(TaskDetailAction.OnCloseManageGroups) },
            )
            if (state.reminderDatePickerOpen) ReminderDatePicker(state.reminderDraft, viewModel::onAction)
            if (state.reminderTimePickerOpen) ReminderTimePicker(state.reminderDraft, viewModel::onAction)
            if (state.shouldShowPermissionDialog) PermissionAlertDialog(viewModel.isXiaomi, viewModel::onAction)
            if (state.shouldShowPermissionDeniedDialog) PermissionDeniedDialog(state, viewModel::onAction)
        })
}

@Composable
fun TaskDetailScreen(state: TaskDetailState, task: Task, onAction: (BaseAction) -> Unit, onBack: () -> Boolean) {
    val accent = LiloExtendedTheme.colors.taskColor
    BaseFormScreen(
        title = if (task.id == null) Res.string.add_task_title else Res.string.edit_task_title,
        accent = accent, saveEnabled = !state.isSaving && !state.isLoading,
        onBack = onBack, onSave = { onAction(TaskDetailAction.OnAddTaskButton(true)) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            if (state.hasError) Text(stringResource(Res.string.task_operation_error), color = MaterialTheme.colorScheme.error)
            if (state.isLoading || state.isSaving) LinearProgressIndicator(Modifier.fillMaxWidth(), color = accent)
            AppOutlineTextField(accentColor = accent,
                leadingIcon = { Icon(Icons.Outlined.Title, null, Modifier.size(20.dp)) },
                textFieldRequired = TextFieldRequired(label = stringResource(Res.string.title_label), value = task.title,
                    hint = stringResource(Res.string.task_title_hint), enabled = !state.isLoading && !state.isSaving,
                    onValueChange = { onAction(TaskDetailAction.OnTitleChanged(it)) }, validationStatus = state.titleError))
            AppOutlineTextField(accentColor = accent, singleLine = false, maxLines = 8,
                textFieldModifier = Modifier.heightIn(min = 144.dp),
                leadingIcon = { Icon(Icons.Outlined.Notes, null, Modifier.size(20.dp)) },
                textFieldRequired = TextFieldRequired(label = stringResource(Res.string.description_label), value = task.description, hint = stringResource(Res.string.task_description_hint),
                    enabled = !state.isLoading && !state.isSaving, onValueChange = { onAction(TaskDetailAction.OnDescriptionChanged(it)) }))
            TaskPrioritySelector(task.priority, accent, enabled = !state.isSaving && !state.isLoading) {
                onAction(TaskDetailAction.OnPrioritySelected(it.title))
            }
            FormSelectionRow(Icons.Outlined.Notifications, stringResource(Res.string.reminder_label),
                reminderLabel(state),
                onClick = { onAction(TaskDetailAction.OnDateReminderIcon) },
                clearContentDescription = stringResource(Res.string.remove_reminder_action),
                enabled = !state.isSaving && !state.isLoading,
                onClear = if (state.reminderModel.reminderStartDate != null) ({ onAction(TaskDetailAction.OnClearReminder) }) else null)
            FormSelectionRow(Icons.Outlined.FolderOpen, stringResource(Res.string.group_field_label),
                state.selectedCategory?.title ?: stringResource(Res.string.no_group_label),
                onClick = { onAction(TaskDetailAction.OnCategoryIcon) }, enabled = !state.isSaving && !state.isLoading)
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun reminderLabel(state: TaskDetailState): String {
    val reminder = state.reminderModel
    val day = reminder.reminderStartDate ?: return stringResource(Res.string.not_set_label)
    val date = Instant.fromEpochMilliseconds(day).toLocalDateTime(TimeZone.currentSystemDefault()).date
    return "$date · ${reminder.reminderHour?.toString()?.padStart(2, '0') ?: "--"}:${reminder.reminderMinute?.toString()?.padStart(2, '0') ?: "--"}"
}

@AppPreviews
@Composable
private fun TaskFormPreview() {
    LiloPreviewWrapper {
        val task = Task(title = "Practice violin", priority = 1)
        TaskDetailScreen(TaskDetailState(task = task), task, {}, { true })
    }
}
