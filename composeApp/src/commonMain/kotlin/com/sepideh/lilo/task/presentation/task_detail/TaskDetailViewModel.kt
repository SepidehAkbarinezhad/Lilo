package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.task.data.TaskDatabase
import com.sepideh.lilo.task.data.toEntity
import com.sepideh.lilo.task.domain.Task
import kotlinx.coroutines.launch

class TaskDetailViewModel(private val taskDatabase: TaskDatabase) : BaseViewModel() {

    var task: Task by mutableStateOf(Task())

    override fun onEvent(event: BaseEvent) {
        super.onEvent(event)
        when (event) {
            is TaskDetailEvent.OnTitleChanged -> {
                task = task.copy(title = event.title)
            }

            is TaskDetailEvent.OnDescriptionChanged -> {
                task = task.copy(description = event.description)
            }

            is TaskDetailEvent.OnAddTask -> {
                viewModelScope.launch { taskDatabase.taskDao().upsert(task.toEntity()) }
            }
        }
    }
}