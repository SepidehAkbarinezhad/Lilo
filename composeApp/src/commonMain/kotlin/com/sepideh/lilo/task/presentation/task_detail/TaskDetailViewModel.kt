package com.sepideh.lilo.task.presentation.task_detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.task.data.TaskDatabase
import com.sepideh.lilo.task.data.category.CategoryDatabase
import com.sepideh.lilo.task.data.category.toCategory
import com.sepideh.lilo.task.data.category.toCategoryList
import com.sepideh.lilo.task.data.toEntity
import com.sepideh.lilo.task.domain.Task
import com.sepideh.lilo.task.presentation.model.Category
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.task.presentation.model.Priority.Companion.priorities
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val taskDatabase: TaskDatabase,
    private val categoryDatabase: CategoryDatabase
) : BaseViewModel() {
    private val _categories = categoryDatabase.categoryDao().getAllCategories()
        .map { it.toCategoryList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())


    private val _state = MutableStateFlow(TaskDetailState())
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
    val state = combine(
        _state,
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
                println("OnSelectedCategoryChanged categories ${state.value.categories}")
                println("OnSelectedCategoryChanged selectedCategory $selectedCategory")
                _state.update { it.copy(selectedCategory = selectedCategory) }
            }

            is TaskDetailEvent.OnSelectedPriorityChanged -> {
                val selectedPriority = Priority.getByTitle(event.title)
                task = task.copy(priority = selectedPriority.id)
            }

            is TaskDetailEvent.OnAddTask -> {
                val task = task.copy(category = state.value.selectedCategory?.id?:Category.categories[0].id)
                viewModelScope.launch { taskDatabase.taskDao().upsert(task.toEntity()) }
            }
        }
    }


}