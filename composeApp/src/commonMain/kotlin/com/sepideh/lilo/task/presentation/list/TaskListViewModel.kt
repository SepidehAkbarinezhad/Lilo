package com.sepideh.lilo.task.presentation.list

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.category.domain.CategoryDomain
import com.sepideh.lilo.category.domain.repository.CategoryRepository
import com.sepideh.lilo.category.presentation.toPresentationList
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import com.sepideh.lilo.task.domain.model.TaskQuery
import com.sepideh.lilo.task.domain.model.TaskSort
import com.sepideh.lilo.task.domain.model.matching
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import com.sepideh.lilo.task.presentation.model.Enums
import com.sepideh.lilo.task.presentation.model.SortOrder
import com.sepideh.lilo.task.presentation.model.TaskFilterOption
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class TaskListViewModel(
    languageProvider: LanguageProvider,
    taskRepository: TaskRepository,
    categoryRepository: CategoryRepository,
    private val mutations: TaskMutations,
) : BaseViewModel() {
    private val local = MutableStateFlow(TaskListState(isLoading = true))
    private val query = local.map { it.searchQuery }.distinctUntilChanged().debounce(250)
    private val tasks = taskRepository.getAllTasks()
        .onEach { local.update { it.copy(isLoading = false) } }
        .catch { local.update { it.copy(isLoading = false, hasError = true) }; emit(emptyList()) }
    private val categories = categoryRepository.getAllCategories()
        .catch { local.update { it.copy(hasError = true) }; emit(emptyList()) }

    // One collector combines every filter; Apply never launches another Room subscription.
    val state = combine(local, tasks, categories, query, languageProvider.languageFlow) { ui, tasks, groups, text, language ->
        val groupId = ui.selectedCategory?.takeIf { id -> groups.any { it.id == id } }
        val filter = ui.taskFilterOption
        ui.copy(
            tasksResult = tasks.matching(TaskQuery(
                text = text,
                groupId = groupId,
                completed = filter.taskStatus.filter { it != Enums.ALL }.map { it == Enums.DONE }.toSet(),
                priorities = filter.priorityList.map { it.value }.toSet(),
                sort = if (ui.sortOrder == SortOrder.Priority) TaskSort.PRIORITY else TaskSort.REMINDER_DATE,
            )),
            categories = (listOf(CategoryDomain.categories.first()) + groups).toPresentationList(language),
            selectedCategory = groupId,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskListState(isLoading = true))

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when (action) {
            is TaskListAction.OnSortOrderChanged -> local.update { it.copy(sortOrder = action.sortOrder) }
            is TaskListAction.OnCategorySelected -> local.update { it.copy(selectedCategory = action.id) }
            is TaskListAction.OnSearchQueryChange -> local.update { it.copy(searchQuery = action.query) }
            is TaskListAction.OnSearchToggle -> local.update { it.copy(isSearchVisible = action.open, searchQuery = if (action.open) it.searchQuery else "") }
            TaskListAction.OnFilterIcon -> local.update { it.copy(isFilterSheetOpen = true, tempFilterOption = it.taskFilterOption) }
            TaskListAction.OnCloseFilterIcon -> local.update { it.copy(isFilterSheetOpen = false, tempFilterOption = it.taskFilterOption) }
            TaskListAction.OnApplyFilter -> local.update { it.copy(isFilterSheetOpen = false, taskFilterOption = it.tempFilterOption) }
            TaskListAction.OnResetFilter -> local.update { it.copy(tempFilterOption = TaskFilterOption()) }
            is TaskListAction.OnStatusFilterChanged -> local.update {
                val values = it.tempFilterOption.taskStatus.toMutableList()
                if (!values.remove(action.status)) values.add(action.status)
                it.copy(tempFilterOption = it.tempFilterOption.copy(taskStatus = values))
            }
            is TaskListAction.OnPriorityFilterChanged -> local.update {
                val values = it.tempFilterOption.priorityList.toMutableList()
                if (!values.remove(action.priority)) values.add(action.priority)
                it.copy(tempFilterOption = it.tempFilterOption.copy(priorityList = values))
            }
            is TaskListAction.OnDeleteTaskIcon -> local.update { it.copy(selectedTask = action.task, isDeleteDialogOpen = true) }
            TaskListAction.OnDismissDeleteDialog -> local.update { it.copy(selectedTask = null, isDeleteDialogOpen = false) }
            TaskListAction.OnDeleteTaskConfirm -> {
                val id = local.value.selectedTask?.id ?: return
                mutate { mutations.delete(id) }
                local.update { it.copy(selectedTask = null, isDeleteDialogOpen = false) }
            }
            is TaskListAction.OnDoneChange -> action.task.id?.let { id -> mutate { mutations.setCompleted(id, action.task.done) } }
        }
    }

    private fun mutate(block: suspend () -> Unit) {
        viewModelScope.launch {
            try { block() } catch (e: CancellationException) { throw e }
            catch (_: Exception) { local.update { it.copy(hasError = true) } }
        }
    }
    override fun onResetState() = Unit
}
