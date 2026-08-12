@file:OptIn(FlowPreview::class)

package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.category.data.local.room.CategoryDatabase
import com.sepideh.lilo.category.data.local.room.toDomainList
import com.sepideh.lilo.category.data.local.room.toEntity
import com.sepideh.lilo.category.domain.CategoryDomain
import com.sepideh.lilo.category.domain.toPresentationList
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.core.utils.setReminderTime
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import com.sepideh.lilo.task.data.Reminder
import com.sepideh.lilo.task.data.local.room.TaskDatabase
import com.sepideh.lilo.task.data.local.room.toEntity
import com.sepideh.lilo.task.data.local.room.toTaskList
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.task.presentation.model.TaskFilterOption
import com.sepideh.lilo.task.presentation.model.Enums
import com.sepideh.lilo.task.presentation.model.SortOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

class TaskListViewModel(
    private val languageProvider: LanguageProvider,
    private val taskRepository: TaskRepository,
    private val categoryDatabase: CategoryDatabase,
    private val reminderScheduler: ReminderScheduler,
) : BaseViewModel() {

    private val _categories: StateFlow<List<CategoryDomain>> =
        categoryDatabase.categoryDao().getAllCategories().onEach { categories ->
            if (categories.isEmpty()) {
                // Perform upsert only if categories are empty after fetching
                upsertCategories()
            }
        }.map { it.toDomainList() }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    /*
    * stateIn is used to collect the combined flow as stateflow within the lifecycle of the viewmodel.
    * WhileSubscribed ensures the flow is only shared while there are active collectors and stop emitting values for up to 5 seconds after the last collector unsubscribed.
    * */
    private val _state = MutableStateFlow(TaskListState())

    /*
    * Using debounce to control rapid search inputs:
    * - Delays filtering by 300ms to avoid processing incomplete queries.
    * - If a new value arrives before the 300ms delay ends, the previous value is discarded.
    * - Ensures only the final query (after typing pauses) triggers the filtering logic.
    * */
    @OptIn(FlowPreview::class)
    private val _debouncedSearchQuery = _state
        .map { it.searchQuery }
        .debounce(300L)
        .distinctUntilChanged()

    val state = combine(
        _state,
        _tasks,
        _categories,
        _debouncedSearchQuery,
        languageProvider.languageFlow
    ) { state, tasks, categories, searchQuery, currentLanguage ->
        val updatedCategories: List<CategoryDomain> =
            listOf(CategoryDomain.categories[0]) + categories // Add "All" as the first item in the list
        val validSelectedCategory = categories.find { it.id == state.selectedCategory }
        state.copy(
            tasksResult = tasks.let { taskList ->
                // If the user hasn't selected a category, treat the "All" category as null
                val filteredBasedOnCategory = if (validSelectedCategory != null) {
                    // Filtering is done on a local list synchronously, so there's no need to show a loading state
                    taskList.filter { task -> task.category == validSelectedCategory.id }
                } else {
                    taskList
                }
                val filtered = filteredBasedOnCategory.filter { task ->
                    task.title.contains(
                        searchQuery,
                        ignoreCase = true
                    ) || task.description.contains(searchQuery, ignoreCase = true)
                }
                val sorted = when (state.sortOrder) {
                    SortOrder.Priority -> filtered.sortedBy { it.priority }
                    SortOrder.Date -> filtered.sortedByDescending { it.startDate ?: 0L }
                }
                sorted
            },
            categories = updatedCategories.toPresentationList(currentLanguage),
            selectedCategory = validSelectedCategory?.id
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TaskListState())


    var newTask: Task? by mutableStateOf(null)
        private set

    private var selectedTask: Task? = null

    init {
        loadTasks()
    }

    private fun loadTasks() {
        onAction(BaseAction.ShowLoading(true))
        viewModelScope.launch {
            taskRepository.getAllTasks()
                .collect { tasksList ->
                    delay(500)
                    _tasks.value = tasksList
                    onAction(BaseAction.ShowLoading(false))
                }
        }
    }

    private fun upsertCategories() {
        viewModelScope.launch {
            CategoryDomain.categories.subList(1, CategoryDomain.categories.size).forEach { item ->
                categoryDatabase.categoryDao().upsert(item.toEntity())
            }
        }
    }

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            is TaskListAction.OnSortOrderChanged -> {
                _state.update { it.copy(sortOrder = action.sortOrder) }
            }

            is TaskListAction.OnCategorySelected -> {
                _state.update {
                    it.copy(selectedCategory = action.id)
                }
            }

            is TaskListAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
                //  _state.value = TaskListState(searchQuery = event.query)
            }

            is TaskListAction.OnFilterIcon -> {
                _state.update { it.copy(isFilterSheetOpen = !it.isFilterSheetOpen) }
            }

            is TaskListAction.OnCloseFilterIcon -> {
                _state.update {
                    it.copy(
                        tempFilterOption = it.taskFilterOption,
                        isFilterSheetOpen = false
                    )
                }
            }

            is TaskListAction.OnApplyFilter -> {
                _state.update {
                    it.copy(
                        taskFilterOption = _state.value.tempFilterOption,
                        isFilterSheetOpen = false
                    )
                }

                viewModelScope.launch {
                    with(state.value.taskFilterOption) {
                        onAction(BaseAction.ShowLoading(true))
                        taskRepository.getTasksByFilter(
                            done = if (taskStatus.isEmpty() || taskStatus.size == 2) null else Enums.DONE in taskStatus,
                            priority = priorityList.map { it.id }
                                .ifEmpty { Priority.priorities.map { it.id } }
                        ).collect { tasksList ->
                            delay(500.milliseconds)
                            _tasks.value = tasksList
                            onAction(BaseAction.ShowLoading(false))
                        }
                    }
                }
            }

            is TaskListAction.OnStatusFilterChanged -> {
                action.status.let {
                    val updatedList =
                        state.value.tempFilterOption.taskStatus.toMutableList().apply {
                            if (contains(action.status)) {
                                remove(it)
                            } else {
                                add(it)
                            }
                        }
                    val tempFilter =
                        state.value.tempFilterOption.copy(taskStatus = updatedList)

                    _state.update { it.copy(tempFilterOption = tempFilter) }
                }
            }

            is TaskListAction.OnPriorityFilterChanged -> {
                action.priority.let {
                    val updatedList =
                        state.value.tempFilterOption.priorityList.toMutableList().apply {
                            if (contains(action.priority)) {
                                remove(it)
                            } else {
                                add(it)
                            }
                        }
                    val tempFilter =
                        state.value.tempFilterOption.copy(priorityList = updatedList)
                    _state.update { it.copy(tempFilterOption = tempFilter) }
                }
            }

            is TaskListAction.OnResetFilter -> {
                _state.update {
                    it.copy(
                        tempFilterOption = TaskFilterOption(),
                        taskFilterOption = TaskFilterOption(),
                    )
                }
            }

            is TaskListAction.OnDeleteTaskIcon -> {
                selectedTask = action.task
                _state.update {
                    it.copy(isDeleteDialogOpen = true)
                }
            }

            is TaskListAction.OnDismissDeleteDialog -> {
                _state.update {
                    it.copy(isDeleteDialogOpen = false)
                }
            }

            is TaskListAction.OnDeleteTaskConfirm -> {
                onAction(BaseAction.ShowLoading(true))
                selectedTask?.let {
                    viewModelScope.launch {
                        delay(500)
                        withContext(Dispatchers.IO) {
                            it.id?.let {id -> taskRepository.deleteTask(id = id) }
                        }
                        onAction(BaseAction.ShowLoading(false))
                    }
                }

            }

            is TaskListAction.OnTitleChanged -> {
                newTask = newTask?.copy(title = action.title)
            }

            is TaskListAction.OnDescriptionChanged -> {
                newTask = newTask?.copy(description = action.value)
            }

            is TaskListAction.OnDoneChange -> {
                if (action.task.done) {
                    action.task.id?.let {
                        with(action.task) {
                            reminderScheduler.cancelReminder(
                                reminder = Reminder(
                                    id = id?.toInt()!!,
                                    title = title,
                                    content = "",
                                    startDate = startDate,
                                    endDate = setReminderTime(
                                        dayMillis = endDate,
                                        hour = hour,
                                        minute = minute
                                    )
                                )
                            )
                        }

                    }

                }
                viewModelScope.launch {
                   taskRepository.upsertTask(task = action.task)
                }
            }

            is TaskListAction.OnPhotoPicked -> {
                newTask = newTask?.copy(photo = action.bytes)
            }

            is TaskListAction.OnSearchToggle -> {
                _state.update {
                    it.copy(isSearchVisible = action.open)
                }
            }

        }
    }


    override fun onResetState() {

    }

}