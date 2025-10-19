@file:OptIn(FlowPreview::class)

package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.task.data.Reminder
import com.sepideh.lilo.task.data.TaskDatabase
import com.sepideh.lilo.task.data.category.CategoryDatabase
import com.sepideh.lilo.task.data.category.toCategoryList
import com.sepideh.lilo.task.data.category.toEntity
import com.sepideh.lilo.task.data.toEntity
import com.sepideh.lilo.task.data.toTaskList
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.presentation.model.Category
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.task.presentation.model.TaskFilterOption
import com.sepideh.lilo.task.presentation.model.TaskStatus
import com.sepideh.lilo.core.utils.setReminderTime
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

class TaskListViewModel(
    private val taskDatabase: TaskDatabase,
    private val categoryDatabase: CategoryDatabase,
    private val reminderScheduler: ReminderScheduler,
    ) : BaseViewModel() {

    private val _categories =
        categoryDatabase.categoryDao().getAllCategories().onEach { categories ->
            if (categories.isEmpty()) {
                // Perform upsert only if categories are empty after fetching
                upsertCategories()
            }
        }
            .map { it.toCategoryList() }
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
        _debouncedSearchQuery
    ) { state, tasks, categories, searchQuery ->
        val updatedCategories =
            listOf(Category.categories[0]) + categories // Add "All" as the first item in the list
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
                filteredBasedOnCategory.filter { task ->
                    task.title.contains(
                        searchQuery,
                        ignoreCase = true
                    ) || task.description.contains(searchQuery, ignoreCase = true)
                }
            },
            categories = updatedCategories,
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
            taskDatabase.taskDao().getAllTasks()
                .collect { tasksList ->
                    delay(500)
                    _tasks.value = tasksList.toTaskList()
                    onAction(BaseAction.ShowLoading(false))
                }
        }
    }

    private fun upsertCategories() {
        viewModelScope.launch {
            Category.categories.subList(1, Category.categories.size).forEach { item ->
                categoryDatabase.categoryDao().upsert(item.toEntity())
            }
        }
    }

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
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
                        taskDatabase.taskDao().getTaskByFilter(
                            done = if (taskStatus.isEmpty()) null else TaskStatus.DONE in taskStatus,
                            priority = priorityList.map { it.id }
                                .ifEmpty { Priority.priorities.map { it.id } }
                        ).collect { tasksList ->
                            delay(500)
                            _tasks.value = tasksList.toTaskList()
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
                            it.id?.let { taskDatabase.taskDao().deleteById(it) }
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
                if(action.task.done){
                    action.task.id?.let{
                        with(action.task){
                            reminderScheduler.cancelReminder( reminder = Reminder(
                                id = id?.toInt()!!,
                                title = title,
                                content = "",
                                startDate = startDate,
                                endDate = setReminderTime(dayMillis = endDate, hour = hour, minute = minute)
                            )
                            )
                        }

                    }

                }
                viewModelScope.launch {
                    taskDatabase.taskDao().upsert(task = action.task.toEntity())
                }
            }

            is TaskListAction.OnPhotoPicked -> {
                newTask = newTask?.copy(photo = action.bytes)
            }

        }
    }


    override fun onResetState() {

    }

}