package com.sepideh.lilo.task.presentation.detail

import com.sepideh.lilo.core.presentation.icons.LiloIcons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.sepideh.lilo.core.presentation.format.localizedDigits
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.task.presentation.reminder.ReminderEditorScreen
import com.sepideh.lilo.task.presentation.reminder.label
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.BaseFormScreen
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.group.GroupOption
import com.sepideh.lilo.core.presentation.components.group.GroupSelectionSheet
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.core.presentation.components.form.FormSelectionRow
import com.sepideh.lilo.core.presentation.components.group.GroupManagementDialog
import com.sepideh.lilo.task.presentation.detail.components.TaskPrioritySelector
import com.sepideh.lilo.task.presentation.detail.components.SaveWithoutReminderDialog
import com.sepideh.lilo.task.presentation.detail.components.PermissionDeniedDialog
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
    androidx.lifecycle.compose.LifecycleEventEffect(androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
        viewModel.onAction(TaskDetailAction.OnPermissionReturn)
    }
    LaunchedEffect(taskId) { taskId?.let { viewModel.onAction(TaskDetailAction.OnGetSelectedTaskInfo(it)) } }
    BaseRoot(viewModel = viewModel, navigateTo = onNavigateTo, onBack = onBack,
        bodyContainer = { TaskDetailScreen(state, state.task, viewModel::onAction, onBack) },
        dialogContent = {
            if (state.categoryDialogOpen) GroupSelectionSheet(
                groups = state.categories.filter { it.isDeletable }.map { GroupOption(it.id, it.title) },
                selectedId = state.draftCategoryId.takeUnless { id -> state.categories.any { it.id == id && !it.isDeletable } },
                accent = LiloExtendedTheme.colors.taskColor,
                onSelect = { viewModel.onAction(TaskDetailAction.OnGroupDraftSelected(it)) },
                onManage = { viewModel.onAction(TaskDetailAction.OnManageGroups) },
                onConfirm = { viewModel.onAction(TaskDetailAction.OnConfirmGroup) },
                onDismiss = { viewModel.onAction(TaskDetailAction.OnDismissCategoryDialog) },
            )
            if (state.groupManagementOpen) GroupManagementDialog(
                selectedId = state.draftCategoryId, isAdding = state.isAddingGroup, addedVersion = state.groupAddedVersion,
                onCreate = { viewModel.onAction(TaskDetailAction.OnAddNewCategory(it)) },
                accent = LiloExtendedTheme.colors.taskColor,
                groups = state.categories.filter { it.isDeletable }.map { GroupOption(it.id, it.title) },
                errorMessage = if (state.hasError) stringResource(Res.string.task_operation_error) else null,
                onDelete = { viewModel.onAction(TaskDetailAction.OnDeleteCategory(it)) },
                onRename = { id, title -> viewModel.onAction(TaskDetailAction.OnRenameCategory(id, title)) },
                onDismiss = { viewModel.onAction(TaskDetailAction.OnCloseManageGroups) },
            )
            state.missingPermission?.let { PermissionDeniedDialog(it, viewModel::onAction) }
            if (state.confirmSaveWithoutReminder) SaveWithoutReminderDialog(viewModel::onAction)
        })
}

@Composable
fun TaskDetailScreen(state: TaskDetailState, task: Task, onAction: (BaseAction) -> Unit, onBack: () -> Boolean) {
    val accent = LiloExtendedTheme.colors.taskColor
    val pickImages = com.sepideh.lilo.core.presentation.components.picker.rememberImagePicker(
        onSelected = { onAction(TaskDetailAction.OnImagesSelected(it)) },
        onError = { onAction(TaskDetailAction.OnImagePickerFailure) })
    val imageCount = state.selectedImages?.size ?: task.imageNames.size
    if (state.reminderEditorOpen) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { onAction(TaskDetailAction.OnDismissReminder) },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        ) {
            ReminderEditorScreen(task.reminderAt, task.repeatRule, task.reminderTimeZoneId, accent,
                onConfirm = { at, repeat, zone -> onAction(TaskDetailAction.OnReminderConfirmed(at, repeat, zone)) },
                onDismiss = { onAction(TaskDetailAction.OnDismissReminder) })
        }
    }
    BaseFormScreen(
        title = if (task.id == null) Res.string.add_task_title else Res.string.edit_task_title,
        accent = accent, textSaveAction = true, saveEnabled = !state.isSaving && !state.isLoading,
        onBack = onBack, onSave = { onAction(TaskDetailAction.OnAddTaskButton()) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(com.sepideh.lilo.ui.theme.LiloSpacing.Screen), verticalArrangement = Arrangement.spacedBy(com.sepideh.lilo.ui.theme.LiloSpacing.Section)) {
            if (state.hasError) Text(stringResource(Res.string.task_operation_error), color = MaterialTheme.colorScheme.error)
            if (state.isLoading || state.isSaving) LinearProgressIndicator(Modifier.fillMaxWidth(), color = accent)
            AppOutlineTextField(accentColor = accent,
                leadingIcon = { Icon(LiloIcons.Edit, null, Modifier.size(com.sepideh.lilo.ui.theme.LiloSize.Icon)) },
                textFieldRequired = TextFieldRequired(label = stringResource(Res.string.title_label), value = task.title,
                    hint = "", enabled = !state.isLoading && !state.isSaving,
                    onValueChange = { onAction(TaskDetailAction.OnTitleChanged(it)) }, validationStatus = state.titleError))
            AppOutlineTextField(accentColor = accent, singleLine = false, maxLines = 8, topAlignedIcon = true,
                textFieldModifier = Modifier.heightIn(min = 84.dp),
                leadingIcon = { Icon(LiloIcons.Description, null, Modifier.size(com.sepideh.lilo.ui.theme.LiloSize.Icon)) },
                textFieldRequired = TextFieldRequired(label = stringResource(Res.string.description_label), value = task.description, hint = "",
                    enabled = !state.isLoading && !state.isSaving, onValueChange = { onAction(TaskDetailAction.OnDescriptionChanged(it)) }))
            TaskPrioritySelector(task.priority, accent, enabled = !state.isSaving && !state.isLoading) {
                onAction(TaskDetailAction.OnPriorityIdSelected(it.id))
            }
            FormSelectionRow(accent = accent, icon = LiloIcons.Groups, title = stringResource(Res.string.group_field_label),
                value = state.selectedCategory?.takeIf { it.isDeletable }?.title ?: stringResource(Res.string.no_group_label),
                onClick = { onAction(TaskDetailAction.OnCategoryIcon) }, enabled = !state.isSaving && !state.isLoading)
            FormSelectionRow(icon = LiloIcons.Reminder, title = stringResource(Res.string.reminder_label), accent = accent,
                value = com.sepideh.lilo.task.presentation.reminder.taskReminderLabel(task, includeRepeat = true),
                onClick = { onAction(TaskDetailAction.OnDateReminderIcon) },
                clearContentDescription = stringResource(Res.string.remove_reminder_action),
                enabled = !state.isSaving && !state.isLoading,
                onClear = if (state.task.reminderAt != null) ({ onAction(TaskDetailAction.OnClearReminder) }) else null)
            FormSelectionRow(icon = LiloIcons.Images, title = stringResource(Res.string.task_images_label), accent = accent,
                value = stringResource(Res.string.task_images_count, imageCount).localizedDigits(androidx.compose.ui.platform.LocalLayoutDirection.current == androidx.compose.ui.unit.LayoutDirection.Rtl), onClick = pickImages,
                enabled = !state.isLoading && !state.isSaving,
                onClear = if (imageCount > 0) ({ onAction(TaskDetailAction.OnClearImages) }) else null,
                clearContentDescription = stringResource(Res.string.task_clear_images))
        }
    }
}


@AppPreviews
@Composable
private fun TaskFormPreview() {
    LiloPreviewWrapper {
        val task = Task(title = "تمرین ساز", priority = 1, imageNames = listOf("sample-a.jpg", "sample-b.jpg"))
        TaskDetailScreen(TaskDetailState(task = task), task, {}, { true })
    }
}
