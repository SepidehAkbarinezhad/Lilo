package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.task.data.TaskDatabase
import com.sepideh.lilo.task.data.toTaskList
import com.sepideh.lilo.task.domain.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TaskListViewModel(private val taskDatabase: TaskDatabase) : ViewModel() {

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

    fun onAction(action: TaskListEvent) {
        when (action) {
            is TaskListEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
                _state.value = TaskListState(searchQuery = action.query)
            }

            is TaskListEvent.OnTabSelected -> {
                _state.update {
                    it.copy(selectedTabIndex = action.index)
                }
            }

            TaskListEvent.DeleteTask -> {}
            TaskListEvent.DismissContact -> {}
            TaskListEvent.OnAddNewTaskClick -> {
                newTask = Task(id = null, title = "", description = "")
            }

            is TaskListEvent.OnTitleChanged -> {
                newTask = newTask?.copy(title = action.value)
            }

            TaskListEvent.OnAddPhotoClicked -> {


            }

            is TaskListEvent.OnDescriptionChanged -> {
                newTask = newTask?.copy(description = action.value)
            }

            is TaskListEvent.OnEditTask -> {

                newTask = action.task
            }

            is TaskListEvent.OnPhotoPicked -> {
                newTask = newTask?.copy(photo = action.bytes)
            }

            TaskListEvent.SaveTask -> {}
        }
    }
}