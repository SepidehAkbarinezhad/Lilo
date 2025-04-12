@file:OptIn(FlowPreview::class)

package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.task.data.TaskDatabase
import com.sepideh.lilo.task.data.category.CategoryDatabase
import com.sepideh.lilo.task.data.category.toCategoryList
import com.sepideh.lilo.task.data.category.toEntity
import com.sepideh.lilo.task.data.toEntity
import com.sepideh.lilo.task.data.toTaskList
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.model.Category
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
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskListViewModel(
    private val taskDatabase: TaskDatabase,
    private val categoryDatabase: CategoryDatabase
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
                println("taskList before filter: $taskList")
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
        println("init loadingTag")
        onEvent(BaseEvent.ShowLoading(true))
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskDatabase.taskDao().getAllTasks()
                .collect { tasksList ->
                    delay(500)
                    _tasks.value = tasksList.toTaskList()
                    onEvent(BaseEvent.ShowLoading(false))
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

    override fun onEvent(event: BaseEvent) {
        super.onEvent(event)
        when (event) {
            is TaskListEvent.OnCategorySelected -> {
                _state.update {
                    it.copy(selectedCategory = event.id)
                }
            }

            is TaskListEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
                //  _state.value = TaskListState(searchQuery = event.query)
            }
            is TaskListEvent.OnFilterIcon->{
                _state.update { it.copy(isFilterSheetOpen = !it.isFilterSheetOpen) }
            }

            TaskListEvent.OnAddNewTaskClick -> {
                newTask = Task()
            }

            is TaskListEvent.OnEditTaskIcon -> {
                newTask = event.task
            }

            is TaskListEvent.OnDeleteTaskIcon -> {
                selectedTask = event.task
                onEvent(BaseEvent.ShowDialog(true))
            }

            is TaskListEvent.OnDeleteTaskConfirm -> {
                selectedTask?.let {
                    viewModelScope.launch {
                        withContext(Dispatchers.IO) {
                            try {
                                taskDatabase.taskDao().deleteById(it.toEntity().id)
                            } catch (e: Exception) {
                                println("exception: ${e.message}")
                            }
                        }
                    }
                }

            }

            is TaskListEvent.OnTitleChanged -> {
                newTask = newTask?.copy(title = event.title)
            }

            is TaskListEvent.OnDescriptionChanged -> {
                newTask = newTask?.copy(description = event.value)
            }

            is TaskListEvent.OnDoneChange -> {
                viewModelScope.launch {
                    taskDatabase.taskDao().upsert(task = event.task.toEntity())
                }
            }

            is TaskListEvent.OnPhotoPicked -> {
                newTask = newTask?.copy(photo = event.bytes)
            }

        }
    }


    override fun onResetState() {

    }

}