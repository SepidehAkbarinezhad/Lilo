package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.task.data.TaskDatabase
import com.sepideh.lilo.task.data.category.CategoryDatabase
import com.sepideh.lilo.task.data.category.toCategoryList
import com.sepideh.lilo.task.data.toEntity
import com.sepideh.lilo.task.domain.Task
import com.sepideh.lilo.task.presentation.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskDatabase: TaskDatabase,
    private val categoryDatabase: CategoryDatabase
) : BaseViewModel() {

    private val _state = MutableStateFlow(TaskDetailState())
    val state = combine(
        _state,
        categoryDatabase.categoryDao().getAllCategories()
    ) { state, categories ->
        println("TaskDetailViewModel state  $categories")
        state.copy(
            categories = categories.toCategoryList()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), TaskDetailState())

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

            is TaskDetailEvent.OnSelectedCategoryChanged -> {
                val selectedCategory = state.value.categories.find { it.title == event.title }?: Category.categories[0]
                _state.update { it.copy(selectedCategory = selectedCategory) }
            }

            is TaskDetailEvent.OnAddTask -> {
                viewModelScope.launch { taskDatabase.taskDao().upsert(task.toEntity()) }
            }
        }
    }
}