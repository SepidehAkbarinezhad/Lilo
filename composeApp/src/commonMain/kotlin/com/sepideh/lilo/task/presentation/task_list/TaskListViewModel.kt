package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sepideh.lilo.task.domain.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskListViewModel : ViewModel() {

    private val _state = MutableStateFlow(TaskListState())
    val state = _state.asStateFlow()

    var newTask : Task? by mutableStateOf(null)
        private set

    fun onAction(action: TaskListEvent){
        when(action){
            is TaskListEvent.OnSearchQueryChange->{
                _state.update { it.copy(searchQuery = action.query) }
                _state.value=TaskListState(searchQuery = action.query)
            }
            is TaskListEvent.OnTabSelected -> {
                _state.update {it.copy(selectedTabIndex = action.index)
                }
            }
            TaskListEvent.DeleteTask -> {}
            TaskListEvent.DismissContact -> {}
            TaskListEvent.OnAddNewTaskClick -> {}
            TaskListEvent.OnAddPhotoClicked -> {}
            is TaskListEvent.OnDescriptionChanged -> {}
            is TaskListEvent.OnEditTask -> {}
            is TaskListEvent.OnPhotoPicked -> {}
            is TaskListEvent.OnSelectTask -> {}
            is TaskListEvent.OnTitleChanged -> {}
            TaskListEvent.SaveTask -> {}
        }
    }
}