package com.sepideh.lilo.task.presentation.detail

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.task.domain.TaskGroupFactory
import com.sepideh.lilo.task.domain.repository.TaskGroupRepository
import com.sepideh.lilo.task.presentation.toPresentationList
import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.task.domain.reminder.ReminderPermissions
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.task.domain.reminder.RepeatRule
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.error_empty_field_label

class TaskDetailViewModel(
    private val taskGroupFactory: TaskGroupFactory,
    private val languageProvider: LanguageProvider,
    private val taskRepository: TaskRepository,
    private val taskGroupRepository: TaskGroupRepository,
    private val mutations: TaskMutations,
    private val permissions: ReminderPermissions,
    private val imageStore: com.sepideh.lilo.core.domain.images.ImageStore,
) : BaseViewModel() {
    private val local = MutableStateFlow(TaskDetailState())
    private var loadedTaskId: Long? = null
    val stateValue = combine(local, taskGroupRepository.getAllGroups(), languageProvider.languageFlow) { state, groups, language ->
        val items = groups.toPresentationList(language)
        state.copy(groups = items, selectedGroup = items.find { it.id == state.task.groupId })
    }.catch { local.update { it.copy(hasError = true) }; emit(local.value) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), local.value)

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            is TaskDetailAction.OnImagesSelected -> if (!local.value.isSaving) local.update { it.copy(selectedImages = action.sources) }
            TaskDetailAction.OnClearImages -> if (!local.value.isSaving) local.update { it.copy(selectedImages = emptyList()) }
            TaskDetailAction.OnImagePickerFailure -> local.update { it.copy(hasError = true) }
            is TaskDetailAction.OnTitleChanged -> local.update { it.copy(task = it.task.copy(title = action.title), titleError = it.titleError.copy(isSuccessful = true, messageId = null)) }
            is TaskDetailAction.OnDescriptionChanged -> local.update { it.copy(task = it.task.copy(description = action.description)) }
            TaskDetailAction.OnGroupIcon -> local.update { it.copy(groupDialogOpen = true, draftGroupId = it.task.groupId.takeIf { id -> id != 0L }) }
            TaskDetailAction.OnDismissGroupDialog -> local.update { it.copy(groupDialogOpen = false) }
            is TaskDetailAction.OnGroupSelected -> local.update { it.copy(draftGroupId = action.group.id) }
            is TaskDetailAction.OnGroupDraftSelected -> local.update { it.copy(draftGroupId = action.id) }
            TaskDetailAction.OnConfirmGroup -> local.update { it.copy(task = it.task.copy(groupId = it.draftGroupId ?: 0), groupDialogOpen = false) }
            is TaskDetailAction.OnPriorityIdSelected -> {
                val priority = Priority.getById(action.id)
                local.update { it.copy(selectedPriority = priority, task = it.task.copy(priority = priority.id),) }
            }
            is TaskDetailAction.OnPrioritySelected -> {
                val priority = Priority.getByTitle(action.title)
                local.update { it.copy(selectedPriority = priority, task = it.task.copy(priority = priority.id),) }
            }
            TaskDetailAction.OnDateReminderIcon -> local.update { it.copy(reminderEditorOpen = true) }
            TaskDetailAction.OnDismissReminder -> local.update { it.copy(reminderEditorOpen = false) }
            is TaskDetailAction.OnReminderConfirmed -> local.update {
                it.copy(task = it.task.copy(reminderAt = action.at, repeatRule = action.repeat, reminderTimeZoneId = action.zoneId), reminderEditorOpen = false)
            }
            TaskDetailAction.OnClearReminder -> local.update {
                it.copy(task = it.task.copy(reminderAt = null, repeatRule = RepeatRule.NONE, reminderTimeZoneId = null))
            }
            is TaskDetailAction.OnAddTaskButton -> save()
            TaskDetailAction.OnAskSaveWithoutReminder -> local.update {
                it.copy(missingPermission = null, awaitingPermissionReturn = false, confirmSaveWithoutReminder = true)
            }
            TaskDetailAction.OnSaveWithoutReminder -> {
                local.update { it.copy(task = it.task.copy(reminderAt = null, repeatRule = RepeatRule.NONE, reminderTimeZoneId = null), missingPermission = null, awaitingPermissionReturn = false, confirmSaveWithoutReminder = false) }
                save()
            }
            is TaskDetailAction.OnGrantPermissionButton -> launchOperation {
                val permission = local.value.missingPermission ?: return@launchOperation
                local.update { it.copy(missingPermission = null, awaitingPermissionReturn = true) }
                permissions.request(permission)
                // iOS authorization can finish without another lifecycle resume.
                if (local.value.awaitingPermissionReturn) {
                    val missing = permissions.missingPermission()
                    if (missing != permission) resumePermissionSave()
                    else local.update { it.copy(missingPermission = missing) }
                }
            }
            TaskDetailAction.OnPermissionReturn -> if (local.value.awaitingPermissionReturn) resumePermissionSave()
            is TaskDetailAction.OnAddNewGroup -> addGroup(action.groupTitle)
            is TaskDetailAction.OnRenameGroup -> renameGroup(action.groupId, action.title)
            is TaskDetailAction.OnGetSelectedTaskInfo -> {
                if (loadedTaskId == action.taskId) return
                loadedTaskId = action.taskId
                local.update { it.copy(isLoading = true) }
                launchOperation {
                    val task = taskRepository.getTaskById(action.taskId) ?: error("Task not found")
                    local.update { it.copy(task = task, selectedPriority = Priority.getById(task.priority), isLoading = false) }
                }
            }

            TaskDetailAction.OnCancelPermissionDialog -> local.update { it.copy(missingPermission = null, awaitingPermissionReturn = false, confirmSaveWithoutReminder = false) }
            TaskDetailAction.OnManageGroups -> local.update { it.copy(groupManagementOpen = true) }
            TaskDetailAction.OnCloseManageGroups -> local.update { it.copy(groupManagementOpen = false) }
            is TaskDetailAction.OnDeleteGroup -> launchOperation {
                if (stateValue.value.groups.none { it.id == action.groupId && it.isDeletable }) return@launchOperation
                taskRepository.clearGroup(action.groupId)
                taskGroupRepository.deleteGroup(action.groupId)
                local.update { it.copy(
                    task = if (it.task.groupId == action.groupId) it.task.copy(groupId = 0) else it.task,
                    draftGroupId = it.draftGroupId.takeUnless { id -> id == action.groupId },
                ) }
            }
        }
    }

    private fun addGroup(title: String) {
        if (title.isBlank() || local.value.isAddingGroup) return
        local.update { it.copy(isAddingGroup = true, hasError = false) }
        launchOperation {
            val normalized = title.trim()
            val existing = stateValue.value.groups.firstOrNull { it.title.equals(normalized, ignoreCase = true) }
            val id = existing?.id ?: taskGroupRepository.addGroup(taskGroupFactory.create(normalized))
            local.update { it.copy(draftGroupId = id, isAddingGroup = false, groupAddedVersion = it.groupAddedVersion + 1) }
        }
    }

    private fun renameGroup(id: Long, title: String) {
        val normalized = title.trim()
        if (normalized.isEmpty() || local.value.isAddingGroup) return
        if (stateValue.value.groups.none { it.id == id && it.isEditable }) return
        local.update { it.copy(isAddingGroup = true, hasError = false) }
        launchOperation {
            val existing = taskGroupRepository.getGroupById(id) ?: error("Group not found")
            if (existing.isDefault) {
                local.update { it.copy(isAddingGroup = false) }
                return@launchOperation
            }
            val renamed = when (languageProvider.currentLanguage) {
                AppLanguage.FA -> existing.copy(titleFa = normalized)
                AppLanguage.EN -> existing.copy(titleEn = normalized)
            }
            taskGroupRepository.addGroup(renamed)
            local.update { it.copy(isAddingGroup = false, groupAddedVersion = it.groupAddedVersion + 1) }
        }
    }

    private fun save() {
        if (local.value.isSaving || local.value.isLoading) return
        if (local.value.task.title.isBlank()) {
            local.update { it.copy(titleError = it.titleError.copy(isSuccessful = false, messageId = Res.string.error_empty_field_label)) }
            return
        }
        local.update { it.copy(isSaving = true, hasError = false) }
        launchOperation {
            val state = local.value
            val missing = permissions.missingPermission()
            val allowed = missing == null
            if (state.task.reminderAt != null && missing != null) {
                local.update { it.copy(isSaving = false, missingPermission = missing) }
                return@launchOperation
            }
            val imageNames = state.selectedImages?.let { imageStore.importImages(it) } ?: state.task.imageNames
            val task = state.task.copy(
                imageNames = imageNames,
                groupId = state.task.groupId.takeIf { id -> stateValue.value.groups.any { it.id == id } } ?: 0,
            )
            val result = mutations.save(task, scheduleReminder = allowed)
            local.update { it.copy(task = task.copy(id = result.id), selectedImages = null, isSaving = false, missingPermission = null, hasError = result.reminderFailed) }
            // The row has committed before old files can be removed. Cleanup does not block saving.
            try { imageStore.removeImages(state.task.imageNames - imageNames.toSet()) }
            catch (e: CancellationException) { throw e }
            catch (_: Exception) { /* Retain inaccessible files rather than fail a committed save. */ }
            if (!result.reminderFailed) onAction(BaseAction.OnNavigateTo(null))
        }
    }

    private fun resumePermissionSave() {
        local.update { it.copy(awaitingPermissionReturn = false) }
        save()
    }

    private fun launchOperation(block: suspend () -> Unit) {
        viewModelScope.launch {
            try { block() } catch (e: CancellationException) { throw e }
            catch (_: Exception) { local.update { it.copy(hasError = true, isSaving = false, isLoading = false, isAddingGroup = false, awaitingPermissionReturn = false) } }
        }
    }
    override fun onResetState() = Unit
}
