package com.sepideh.lilo.task.presentation.task_list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TaskListViewModel : ViewModel() {

    private val _state = MutableStateFlow(TaskListState())
    val state = _state.asStateFlow()

    fun onAction(action: TaskListAction){
        when(action){
            is TaskListAction.OnTaskClick->{}
            is TaskListAction.OnSearchQueryChange->{
                _state.update { it.copy(searchQuery = action.query) }
                _state.value=TaskListState(searchQuery = action.query)
            }
        }
    }
}