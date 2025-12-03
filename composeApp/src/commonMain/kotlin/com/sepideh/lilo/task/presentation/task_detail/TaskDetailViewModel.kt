package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.service.PermissionManager
import com.sepideh.lilo.core.domain.ValidateField
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.task.data.Reminder
import com.sepideh.lilo.task.data.local.room.TaskDatabase
import com.sepideh.lilo.category.data.local.room.CategoryDatabase
import com.sepideh.lilo.category.data.local.room.toDomain
import com.sepideh.lilo.category.data.local.room.toDomainList
import com.sepideh.lilo.category.data.local.room.toEntity
import com.sepideh.lilo.category.domain.CategoryFactory
import com.sepideh.lilo.task.data.local.room.toEntity
import com.sepideh.lilo.task.data.local.room.toTask
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.category.domain.CategoryDomain
import com.sepideh.lilo.category.domain.toPresentation
import com.sepideh.lilo.category.domain.toPresentationList
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.core.utils.setReminderTime
import com.sepideh.lilo.settings.domain.LanguageProvider
import com.sepideh.lilo.task.presentation.reminder.ReminderModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val categoryFactory: CategoryFactory,
    private val languageProvider: LanguageProvider,
    private val taskDatabase: TaskDatabase,
    private val categoryDatabase: CategoryDatabase,
    private val reminderScheduler: ReminderScheduler,
    private val permissionManager: PermissionManager
) : BaseViewModel() {

    val currentLanguage = languageProvider.currentLanguage
    val isXiaomi = permissionManager.isXiaomi()
    private val _categories = categoryDatabase.categoryDao().getAllCategories()
        .map {
            println("categoryDatabase $it")

            it.toDomainList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val state = MutableStateFlow(TaskDetailState(selectedCategory = CategoryDomain.first.toPresentation(languageProvider.currentLanguage)))

    /*
  * `combine`:
  * 1. Any update to any of the combined flows triggers the block to execute again.
  *    To avoid unnecessary computations (e.g., Room queries, network requests),
  *    the categories fetching is decoupled from Room.
  * 2. Emits as soon as any flow emits, even if others haven't yet. Since _state starts with
  *    TaskDetailState() (with an empty list) and _categories takes time to emit from Room,
  *    calling `.first()` on an empty list could cause a crash.
  *    Therefore, use `firstOrNull()` for safety.
  */
    val stateValue = combine(
        state,
        _categories,
    ) { state, categories ->
        println("categories $categories")

        // On Room update: Retain the selected category if it still exists; otherwise, select the first item in the list.
        val validSelectedCategory = categories.find { it.id == state.selectedCategory.id }
            ?: CategoryDomain.first
        state.copy(
            categories = categories.toPresentationList(currentLanguage), selectedCategory = validSelectedCategory.toPresentation(currentLanguage)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), state.value)

    var task: Task by mutableStateOf(Task())

    var reminderModel: ReminderModel = ReminderModel()


    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            is TaskDetailAction.OnTitleChanged -> {
                task = task.copy(title = action.title)
            }

            is TaskDetailAction.OnDescriptionChanged -> {
                task = task.copy(description = action.description)
            }

            is TaskDetailAction.OnCategoryIcon -> {
                state.update {
                    it.copy(categoryDialogOpen = true)
                }
            }

            is TaskDetailAction.OnDismissCategoryDialog -> {
                state.update {
                    it.copy(categoryDialogOpen = false)
                }
            }

            is TaskDetailAction.OnPriorityIcon -> {
                state.update {
                    it.copy(priorityDialogOpen = true)
                }
            }

            is TaskDetailAction.OnDismissPriorityDialog -> {
                state.update {
                    it.copy(priorityDialogOpen = false)
                }
            }

            is TaskDetailAction.OnDateReminderIcon -> {
                /*
                * When the user taps the reminder icon, check both alarm and notification permissions.
                * If both permissions are granted, open the reminder dialog.
                * If either permission is missing, a dialog will be shown to inform the user and possibly redirect to settings.
                * */
                viewModelScope.launch {
                    updatePermissionState(checkDeniedPermission = false).await()
                    if (!state.value.shouldShowPermissionDialog) {
                        setReminderDateDialogOpen(open = true)
                    }
                }
            }

            is TaskDetailAction.OnDismissDatePickerButton -> {
                setReminderDateDialogOpen(open = false)
            }

            is TaskDetailAction.OnDismissTimePickerButton -> {
                reminderModel = ReminderModel()
                setReminderTimeDialogOpen(open = false)
            }

            is TaskDetailAction.OnReminderDateConfirm -> {
                reminderModel = action.reminderModel
                setReminderDateDialogOpen(open = false)
                setReminderTimeDialogOpen(open = true)
            }

            is TaskDetailAction.OnReminderTimeConfirm -> {
                reminderModel = action.reminderModel
                setReminderTimeDialogOpen(open = false)
            }

            is TaskDetailAction.OnCategorySelected -> {
                val selectedCategoryDomain = stateValue.value.categories.find { it == action.category }
                    ?: CategoryDomain.categories[0].toPresentation(currentLanguage)
                state.update { it.copy(selectedCategory = selectedCategoryDomain) }
                onAction(TaskDetailAction.OnDismissCategoryDialog)
            }

            is TaskDetailAction.OnPrioritySelected -> {
                val selectedPriority = Priority.getByTitle(action.title)
                state.update { it.copy(selectedPriority = selectedPriority) }
                onAction(TaskDetailAction.OnDismissPriorityDialog)
            }

            is TaskDetailAction.OnSelectReminderTime -> {
                with(action.time) {
                    reminderModel = reminderModel.copy(hour = first, minute = second)
                }
            }

            is TaskDetailAction.OnAddTaskButton -> {
                viewModelScope.launch {
                    val tempTask = task.copy(
                        category = stateValue.value.selectedCategory?.id
                            ?: CategoryDomain.categories[0].id,
                        priority = stateValue.value.selectedPriority.id,
                        hour = reminderModel.hour,
                        minute = reminderModel.minute,
                        startDate = reminderModel.startDay,
                        endDate = reminderModel.endDay,
                        id = task.id
                    )

                    viewModelScope.launch {
                        if (isFormValid(checkDeniedPermission = action.checkDeniedPermission)) {
                            //Room's @Upsert returns:New ID if inserted and -1 if existing task was updated
                            val resultId = taskDatabase.taskDao().upsert(tempTask.toEntity())
                            //Use the correct ID for scheduling a reminder:
                            //If resultId == -1, it's an update, so use existing task.id ,Otherwise, it's a new insert, so use the returned ID
                            val actualId = if (resultId == -1L) tempTask.id!! else resultId
                            startReminder(actualId)
                            onAction(BaseAction.OnNavigateTo(route = null))
                        }
                    }
                }
            }

            is TaskDetailAction.OnAddNewCategory -> {
                viewModelScope.launch {
                    categoryDatabase.categoryDao().upsert(category = categoryFactory.create(action.categoryTitle).toEntity())
                }
                //state.update { it.copy(selectedCategory) }
            }

            is TaskDetailAction.OnGetSelectedTaskInfo -> {
                viewModelScope.launch {
                    taskDatabase.taskDao().getTaskById(action.taskId)?.toTask()
                        ?.let { selectedTask ->
                            task = selectedTask
                            updateSelectedCategory(selectedTask.category)
                            updateSelectedPriority(selectedTask.priority)
                            updateReminder(task = selectedTask)
                        }
                }
            }

            is TaskDetailAction.OnGrantPermissionButton -> {
                closePermissionDialog()
                viewModelScope.launch {
                    with(permissionManager) {
                        when (action.firstTime) {
                            true -> requestNeededPermission()
                            false -> requestDeniedPermission()
                        }
                    }
                }
            }

            TaskDetailAction.OnCancelPermissionDialog -> {
                closePermissionDialog()
            }
        }
    }

    private fun closePermissionDialog() {
        viewModelScope.launch {
            state.update {
                it.copy(
                    shouldShowPermissionDialog = false,
                    shouldShowPermissionDeniedDialog = false
                )
            }
        }
    }


    private suspend fun updateSelectedCategory(categoryId: Long) {
        categoryDatabase.categoryDao().getCategoryById(categoryId = categoryId)
            ?.let { selectedCategory ->
                state.update {
                    it.copy(selectedCategory = selectedCategory.toDomain().toPresentation(currentLanguage))
                }
            }
    }

    private fun updateSelectedPriority(priorityId: Int) {
        state.update {
            it.copy(selectedPriority = Priority.priorities[priorityId])
        }
    }

    private fun updateReminder(task: Task) {
        with(task) {
            reminderModel = reminderModel.copy(
                hour = hour,
                minute = minute,
                startDay = startDate,
                endDay = endDate
            )
        }
    }

    override fun onResetState() {

    }

    private fun updatePermissionState(checkDeniedPermission: Boolean): Deferred<Unit> {
        val deferred = viewModelScope.async {
            val hasAlarm = permissionManager.hasAlarmPermission()
            val hasNotification = permissionManager.hasNotificationPermission()
            val shouldShowPermissionDialog = when (checkDeniedPermission) {
                true -> false
                false -> if (isXiaomi) {
                    !hasNotification
                } else {
                    !hasAlarm || !hasNotification
                }
            }

            //in case of user deny to get permission even when
            val shouldShowPermissionDeniedDialog = when (checkDeniedPermission) {
                true -> {
                    if (isXiaomi) {
                        reminderModel.hour != null && !hasNotification
                    } else {
                        reminderModel.hour != null && (!hasNotification || !hasAlarm)
                    }
                }

                else -> {
                    false
                }
            }

            state.update {
                it.copy(
                    shouldShowPermissionDialog = shouldShowPermissionDialog,
                    shouldShowPermissionDeniedDialog = shouldShowPermissionDeniedDialog
                )
            }
        }
        return deferred
    }

    //todo set reminder in a way can add custom title description
    private fun startReminder(taskId: Long) {
        with(reminderModel) {
            setReminderTime(dayMillis = startDay, hour = hour, minute = minute)?.let {
                reminderScheduler.scheduleReminder(
                    reminder = Reminder(
                        id = taskId.toInt(),
                        title = task.title,
                        content = "",
                        startDate = it,
                        endDate = setReminderTime(dayMillis = endDay, hour = hour, minute = minute)
                    )
                )
            }
        }
    }

    private suspend fun isFormValid(checkDeniedPermission: Boolean): Boolean {
        updatePermissionState(checkDeniedPermission = checkDeniedPermission).await()

        /*
        * Validate the title and description fields based on current input
        * We store these locally to ensure we can use them immediately for logic,
        *  because the state won't reflect updates right away.
        * */
        val newTitleError = ValidateField.validate(
            validationStatus = stateValue.value.titleError.copy(
                value = task.title
            )
        )
        val newDescriptionError = ValidateField.validate(
            validationStatus = stateValue.value.descriptionError.copy(
                value = task.description
            )
        )

        state.update {
            it.copy(
                titleError = newTitleError,
                descriptionError = newDescriptionError,
            )
        }

        return newTitleError.isSuccessful && newDescriptionError.isSuccessful && !state.value.shouldShowPermissionDeniedDialog
    }

    private fun setReminderDateDialogOpen(open: Boolean) {
        state.update {
            it.copy(reminderDatePickerOpen = open)
        }
    }

    private fun setReminderTimeDialogOpen(open: Boolean) {
        state.update {
            it.copy(reminderTimePickerOpen = open)
        }
    }

}