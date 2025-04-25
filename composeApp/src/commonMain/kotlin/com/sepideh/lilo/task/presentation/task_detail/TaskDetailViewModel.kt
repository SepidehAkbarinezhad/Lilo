package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.core.domain.PermissionManager
import com.sepideh.lilo.core.domain.ValidateField
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.task.data.Reminder
import com.sepideh.lilo.task.data.TaskDatabase
import com.sepideh.lilo.task.data.category.CategoryDatabase
import com.sepideh.lilo.task.data.category.toCategory
import com.sepideh.lilo.task.data.category.toCategoryList
import com.sepideh.lilo.task.data.category.toEntity
import com.sepideh.lilo.task.data.toEntity
import com.sepideh.lilo.task.data.toTask
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.reminder.setReminderTime
import com.sepideh.lilo.task.presentation.model.Category
import com.sepideh.lilo.task.presentation.model.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskDatabase: TaskDatabase,
    private val categoryDatabase: CategoryDatabase,
    private val reminderScheduler: ReminderScheduler,
    private val permissionManager: PermissionManager
) : BaseViewModel() {
    private val _categories = categoryDatabase.categoryDao().getAllCategories()
        .map { it.toCategoryList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())


    private val state = MutableStateFlow(TaskDetailState())

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
        // On Room update: Retain the selected category if it still exists; otherwise, select the first item in the list.
        val validSelectedCategory = categories.find { it.id == state.selectedCategory?.id }
            ?: categories.firstOrNull()
        state.copy(
            categories = categories, selectedCategory = validSelectedCategory
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TaskDetailState())

    var task: Task by mutableStateOf(Task())

    var reminderModel: ReminderModel = ReminderModel()


    override fun onEvent(event: BaseEvent) {
        super.onEvent(event)
        when (event) {
            is TaskDetailEvent.OnTitleChanged -> {
                task = task.copy(title = event.title)
            }

            is TaskDetailEvent.OnDescriptionChanged -> {
                task = task.copy(description = event.description)
            }

            is TaskDetailEvent.OnCategoryIcon -> {
                state.update {
                    it.copy(isCategoryDialogOpen = true)
                }
            }

            is TaskDetailEvent.OnDismissCategoryDialog -> {
                state.update {
                    it.copy(isCategoryDialogOpen = false)
                }
            }

            is TaskDetailEvent.OnPriorityIcon -> {
                state.update {
                    it.copy(isPriorityDialogOpen = true)
                }
            }

            is TaskDetailEvent.OnDismissPriorityDialog -> {
                state.update {
                    it.copy(isPriorityDialogOpen = false)
                }
            }

            is TaskDetailEvent.OnDateIcon -> {
                state.update {
                    it.copy(isDateDialogOpen = true)
                }
            }

            is TaskDetailEvent.OnDismissDateDialog -> {
                state.update {
                    it.copy(isDateDialogOpen = false)
                }
            }

            is TaskDetailEvent.OnTimeIcon -> {
                // When the user taps the time (reminder) icon, check both alarm and notification permissions.
                // If both permissions are granted, open the time picker dialog.
                // If either permission is missing, a dialog will be shown to inform the user and possibly redirect to settings.

                viewModelScope.launch {
                    val hasAlarm = permissionManager.hasAlarmPermission()
                    val hasNotif = permissionManager.hasNotificationPermission()
                    state.update {
                        it.copy(
                            hasAlarmPermission = hasAlarm,
                            hasNotificationPermission = hasNotif,
                            isTimeDialogOpen = hasAlarm && hasNotif,
                            shouldShowPermissionDialog = !hasAlarm || !hasNotif
                        )
                    }
                }
            }

            is TaskDetailEvent.OnDismissTimeDialog -> {
                state.update {
                    it.copy(isTimeDialogOpen = false)
                }
            }

            is TaskDetailEvent.OnCategorySelected -> {
                val selectedCategory = stateValue.value.categories.find { it.title == event.title }
                    ?: Category.categories[0]
                state.update { it.copy(selectedCategory = selectedCategory) }
                onEvent(TaskDetailEvent.OnDismissCategoryDialog)

            }

            is TaskDetailEvent.OnPrioritySelected -> {
                val selectedPriority = Priority.getByTitle(event.title)
                state.update { it.copy(selectedPriority = selectedPriority) }
                onEvent(TaskDetailEvent.OnDismissPriorityDialog)
            }

            is TaskDetailEvent.OnSelectReminderDate -> {
                println("OnSelectReminderDate ${event.date}")
                with(event.date) {
                    println("OnSelectReminderDate......... ${event.date.first}  ${event.date.second}")
                    reminderModel = reminderModel.copy(startDay = first, endDay = second)
                }
                println("OnSelectReminderDate $reminderModel")
            }

            is TaskDetailEvent.OnSelectReminderTime -> {
                with(event.time) {
                    println("OnSelectReminderTime-> ${event.time.first} ${event.time.second}")
                    reminderModel = reminderModel.copy(hour = first, minute = second)
                }
            }

            is TaskDetailEvent.OnAddTaskButton -> {
                val task = task.copy(
                    category = stateValue.value.selectedCategory?.id ?: Category.categories[0].id,
                    priority = stateValue.value.selectedPriority.id,
                    hour = reminderModel.hour,
                    minute = reminderModel.minute,
                    startDate = reminderModel.startDay,
                    endDate = reminderModel.endDay,
                )

                if (isFormValid()) {
                    viewModelScope.launch {
                        val id = taskDatabase.taskDao().upsert(task.toEntity())
                        startReminder(id)
                    }
                    onEvent(BaseEvent.OnNavigateTo(AppDestinations.NavigateUp()))
                }
            }

            is TaskDetailEvent.OnAddNewCategory -> {
                viewModelScope.launch {
                    categoryDatabase.categoryDao().upsert(category = event.category.toEntity())
                }
                state.update { it.copy(selectedCategory = null) }
            }

            is TaskDetailEvent.OnGetSelectedTaskInfo -> {
                viewModelScope.launch {
                    taskDatabase.taskDao().getTaskById(event.taskId)?.toTask()
                        ?.let { selectedTask ->
                            println("OnGetSelectedTaskInfo ->  $selectedTask")
                            task = selectedTask
                            updateSelectedCategory(selectedTask.category)
                            updateSelectedPriority(selectedTask.priority)
                            updateReminder(task = selectedTask)
                        }
                }
            }
        }
    }

    private suspend fun updateSelectedCategory(categoryId: Long) {
        categoryDatabase.categoryDao().getCategoryById(categoryId = categoryId)
            ?.let { selectedCategory ->
                state.update {
                    it.copy(selectedCategory = selectedCategory.toCategory())
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

    //todo set reminder in a way can add custom title description
    private fun startReminder(taskId: Long) {
        println("startReminder  $reminderModel ")
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

    private fun isFormValid(): Boolean {
        state.update {
            it.copy(
                titleError = ValidateField.validate(
                    validationStatus = stateValue.value.titleError.copy(
                        value = task.title
                    )
                ),
                descriptionError = ValidateField.validate(
                    validationStatus = stateValue.value.descriptionError.copy(
                        value = task.description
                    )
                )

            )
        }
        return with(stateValue.value) { titleError.isSuccessful && descriptionError.isSuccessful }
    }

}