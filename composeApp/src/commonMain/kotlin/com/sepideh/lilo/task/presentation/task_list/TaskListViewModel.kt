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
import com.sepideh.lilo.task.domain.Task
import com.sepideh.lilo.task.presentation.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskListViewModel(
    private val taskDatabase: TaskDatabase,
    private val categoryDatabase: CategoryDatabase
) : BaseViewModel() {

    /*
    * stateIn is used to collect the combined flow as stateflow within the lifecycle of the viewmodel.
    * WhileSubscribed ensures the flow is only shared while there are active collectors and stop emitting values for up to 5 seconds after the last collector unsubscribed.
    * */
    private val _state = MutableStateFlow(TaskListState())
    val state = combine(
        _state,
        taskDatabase.taskDao().getAllTasks()
    ) { state, tasks ->
        state.copy(
            searchResults = tasks.toTaskList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TaskListState())

    var newTask: Task? by mutableStateOf(null)
        private set

    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            println("categories: init")
            categoryDatabase.categoryDao().getAllCategories().collect { list ->
                list.ifEmpty {
                    println("categories: empty")
                    Category.categories.forEach { item ->
                        categoryDatabase.categoryDao().upsert(item.toEntity())
                    }
                }
                _categories.value = list.toCategoryList()
                println("categories: ${categories.value}")
            }
        }


    }

    override fun onEvent(event: BaseEvent) {
        super.onEvent(event)
        when (event) {
            is TaskListEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
                _state.value = TaskListState(searchQuery = event.query)
            }

            is TaskListEvent.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTabIndex = event.index)
                }
            }

            is TaskListEvent.OnDeleteTask -> {
                println("TaskListEvent.OnDeleteTask->> ${event.task.id}  ${event.task.title}")

                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        try {
                            println("Deleting task with ID: ${event.task.toEntity().id}")

                            taskDatabase.taskDao().deleteById(event.task.toEntity().id)
                            val id = taskDatabase.taskDao().getTaskById(event.task.id ?: 0)
                            println("TaskListEvent.OnDeleteTask->> ${event.task.id}  ${event.task.title}  id is $id")
                        } catch (e: Exception) {
                            println("exception: ${e.message}")
                        }

                    }

                }

            }

            TaskListEvent.DismissContact -> {}
            TaskListEvent.OnAddNewTaskClick -> {
                newTask = Task()
            }

            is TaskListEvent.OnTitleChanged -> {
                newTask = newTask?.copy(title = event.value)
            }

            TaskListEvent.OnAddPhotoClicked -> {


            }

            is TaskListEvent.OnDescriptionChanged -> {
                newTask = newTask?.copy(description = event.value)
            }

            is TaskListEvent.OnEditTask -> {

                newTask = event.task
            }

            is TaskListEvent.OnPhotoPicked -> {
                newTask = newTask?.copy(photo = event.bytes)
            }

            TaskListEvent.SaveTask -> {}
        }
    }
}