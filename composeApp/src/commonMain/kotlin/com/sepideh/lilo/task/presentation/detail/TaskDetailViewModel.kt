package com.sepideh.lilo.task.presentation.detail

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.category.domain.CategoryFactory
import com.sepideh.lilo.category.domain.repository.CategoryRepository
import com.sepideh.lilo.category.presentation.toPresentationList
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
    private val categoryFactory: CategoryFactory,
    private val languageProvider: LanguageProvider,
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val mutations: TaskMutations,
    private val permissions: ReminderPermissions,
    private val imageStore: com.sepideh.lilo.core.domain.images.ImageStore,
) : BaseViewModel() {
    private val local = MutableStateFlow(TaskDetailState())
    private var loadedTaskId: Long? = null
    val stateValue = combine(local, categoryRepository.getAllCategories(), languageProvider.languageFlow) { state, groups, language ->
        val items = groups.toPresentationList(language)
        state.copy(categories = items, selectedCategory = items.find { it.id == state.task.category })
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
            TaskDetailAction.OnCategoryIcon -> local.update { it.copy(categoryDialogOpen = true, draftCategoryId = it.task.category.takeIf { id -> id != 0L }) }
            TaskDetailAction.OnDismissCategoryDialog -> local.update { it.copy(categoryDialogOpen = false) }
            is TaskDetailAction.OnCategorySelected -> local.update { it.copy(draftCategoryId = action.category.id) }
            is TaskDetailAction.OnGroupDraftSelected -> local.update { it.copy(draftCategoryId = action.id) }
            TaskDetailAction.OnConfirmGroup -> local.update { it.copy(task = it.task.copy(category = it.draftCategoryId ?: 0), categoryDialogOpen = false) }
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
            is TaskDetailAction.OnAddNewCategory -> addGroup(action.categoryTitle)
            is TaskDetailAction.OnRenameCategory -> renameGroup(action.categoryId, action.title)
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
            TaskDetailAction.OnManageGroups -> local.update { it.copy(categoryDialogOpen = false, groupManagementOpen = true) }
            TaskDetailAction.OnCloseManageGroups -> local.update { it.copy(categoryDialogOpen = true, groupManagementOpen = false) }
            is TaskDetailAction.OnDeleteCategory -> launchOperation {
                taskRepository.clearGroup(action.categoryId)
                categoryRepository.deleteCategory(action.categoryId)
                local.update { it.copy(
                    task = if (it.task.category == action.categoryId) it.task.copy(category = 0) else it.task,
                    draftCategoryId = it.draftCategoryId.takeUnless { id -> id == action.categoryId },
                ) }
            }
        }
    }

    private fun addGroup(title: String) {
        if (title.isBlank() || local.value.isAddingGroup) return
        local.update { it.copy(isAddingGroup = true, hasError = false) }
        launchOperation {
            val normalized = title.trim()
            val existing = stateValue.value.categories.firstOrNull { it.title.equals(normalized, ignoreCase = true) }
            val id = existing?.id ?: categoryRepository.addCategory(categoryFactory.create(normalized))
            local.update { it.copy(draftCategoryId = id, isAddingGroup = false, groupAddedVersion = it.groupAddedVersion + 1) }
        }
    }

    private fun renameGroup(id: Long, title: String) {
        val normalized = title.trim()
        if (normalized.isEmpty() || local.value.isAddingGroup) return
        local.update { it.copy(isAddingGroup = true, hasError = false) }
        launchOperation {
            val existing = categoryRepository.getCategoryById(id) ?: error("Group not found")
            // Renaming updates the user-visible name in both locales while retaining its stable ID.
            categoryRepository.addCategory(existing.copy(titleEn = normalized, titleFa = normalized))
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
                category = state.task.category.takeIf { id -> stateValue.value.categories.any { it.id == id } } ?: 0,
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
