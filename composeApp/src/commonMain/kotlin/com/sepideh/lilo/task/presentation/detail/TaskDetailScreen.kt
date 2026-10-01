package com.sepideh.lilo.task.presentation.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.selection.GroupOption
import com.sepideh.lilo.core.presentation.components.selection.GroupSelectionSheet
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.detail.components.PermissionAlertDialog
import com.sepideh.lilo.task.presentation.detail.components.PermissionDeniedDialog
import com.sepideh.lilo.task.presentation.model.Priority
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
            if (state.groupManagementOpen) GroupManagementDialog(state, viewModel::onAction)
            if (state.reminderDatePickerOpen) ReminderDatePicker(state.reminderDraft, viewModel::onAction)
            if (state.reminderTimePickerOpen) ReminderTimePicker(state.reminderDraft, viewModel::onAction)
            if (state.shouldShowPermissionDialog) PermissionAlertDialog(viewModel.isXiaomi, viewModel::onAction)
            if (state.shouldShowPermissionDeniedDialog) PermissionDeniedDialog(state, viewModel::onAction)
        })
}

@Composable
fun TaskDetailScreen(state: TaskDetailState, task: Task, onAction: (BaseAction) -> Unit, onBack: () -> Boolean) {
    val accent = LiloExtendedTheme.colors.taskColor
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onBack() }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(Res.string.cancel_button), modifier = Modifier.size(20.dp)) }
            Text(stringResource(if (task.id == null) Res.string.add_task_title else Res.string.edit_task_title), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Button(onClick = { onAction(TaskDetailAction.OnAddTaskButton(true)) }, enabled = !state.isSaving && !state.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp)) {
                Text(stringResource(Res.string.save_task_action))
            }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            if (state.hasError) Text(stringResource(Res.string.task_operation_error), color = MaterialTheme.colorScheme.error)
            if (state.isLoading || state.isSaving) LinearProgressIndicator(Modifier.fillMaxWidth(), color = accent)
            AppOutlineTextField(accentColor = accent,
                leadingIcon = { Icon(Icons.Outlined.Edit, null, Modifier.size(20.dp)) },
                textFieldRequired = TextFieldRequired(label = stringResource(Res.string.title_label), value = task.title,
                    hint = stringResource(Res.string.task_title_hint), enabled = !state.isLoading && !state.isSaving,
                    onValueChange = { onAction(TaskDetailAction.OnTitleChanged(it)) }, validationStatus = state.titleError))
            AppOutlineTextField(accentColor = accent, singleLine = false, maxLines = 8,
                textFieldModifier = Modifier.heightIn(min = 132.dp),
                leadingIcon = { Icon(Icons.Outlined.Notes, null, Modifier.size(20.dp)) },
                textFieldRequired = TextFieldRequired(label = stringResource(Res.string.description_optional), value = task.description,
                    enabled = !state.isLoading && !state.isSaving, onValueChange = { onAction(TaskDetailAction.OnDescriptionChanged(it)) }))
            Column {
                Text(stringResource(Res.string.priority_label), style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Priority.priorities.forEach { priority ->
                        FilterChip(selected = task.priority == priority.id, onClick = { onAction(TaskDetailAction.OnPrioritySelected(priority.title)) },
                            enabled = !state.isSaving, label = { Text(stringResource(priority.title)) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accent.copy(alpha = .16f), selectedLabelColor = MaterialTheme.colorScheme.onSurface))
                    }
                }
            }
            TaskOptionRow(Icons.Outlined.Notifications, stringResource(Res.string.reminder_label),
                reminderLabel(state), state.reminderModel.reminderStartDate != null, accent,
                onClick = { onAction(TaskDetailAction.OnDateReminderIcon) },
                onClear = if (state.reminderModel.reminderStartDate != null) ({ onAction(TaskDetailAction.OnClearReminder) }) else null)
            TaskOptionRow(Icons.Outlined.FolderOpen, stringResource(Res.string.category_label),
                state.selectedCategory?.title ?: stringResource(Res.string.no_group_label), state.selectedCategory != null, accent,
                onClick = { onAction(TaskDetailAction.OnCategoryIcon) })
        }
    }
}

@Composable
private fun TaskOptionRow(icon: ImageVector, title: String, value: String, selected: Boolean, accent: Color, onClick: () -> Unit, onClear: (() -> Unit)? = null) {
    Surface(onClick = onClick, shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .6f))) {
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, Modifier.size(20.dp), tint = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.bodyMedium)
            }
            if (onClear != null) IconButton(onClick = onClear) { Icon(Icons.Outlined.Close, stringResource(Res.string.remove_reminder_action), Modifier.size(18.dp)) }
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

@Composable
private fun GroupManagementDialog(state: TaskDetailState, onAction: (BaseAction) -> Unit) {
    var deleteId by remember { mutableStateOf<Long?>(null) }
    AlertDialog(onDismissRequest = { onAction(TaskDetailAction.OnCloseManageGroups) },
        title = { Text(stringResource(Res.string.manage_groups_action)) },
        text = {
            Column(Modifier.heightIn(max = 360.dp).verticalScroll(rememberScrollState())) {
                if (state.hasError) Text(stringResource(Res.string.task_operation_error), color = MaterialTheme.colorScheme.error)
                state.categories.forEach { group ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(group.title, Modifier.weight(1f))
                        if (group.isDeletable) IconButton(onClick = { deleteId = group.id }) { Icon(Icons.Outlined.Delete, stringResource(Res.string.delete_action), Modifier.size(20.dp)) }
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { onAction(TaskDetailAction.OnCloseManageGroups) }) { Text(stringResource(Res.string.confirm_action)) } })
    deleteId?.let { id ->
        AlertDialog(onDismissRequest = { deleteId = null }, text = { Text(stringResource(Res.string.delete_group_message)) },
            confirmButton = { TextButton(onClick = { onAction(TaskDetailAction.OnDeleteCategory(id)); deleteId = null }) { Text(stringResource(Res.string.delete_action)) } },
            dismissButton = { TextButton(onClick = { deleteId = null }) { Text(stringResource(Res.string.cancel_button)) } })
    }
}
