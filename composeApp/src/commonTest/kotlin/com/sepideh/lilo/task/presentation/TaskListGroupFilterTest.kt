package com.sepideh.lilo.task.presentation

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.domain.model.AppTheme
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import com.sepideh.lilo.task.domain.TaskGroup
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.Reminder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.repository.TaskGroupRepository
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import com.sepideh.lilo.task.presentation.list.TaskListAction
import com.sepideh.lilo.task.presentation.list.TaskListViewModel
import com.sepideh.lilo.task.presentation.model.Enums
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListGroupFilterTest {
    private val store = ViewModelStore()
    private val groups = MutableStateFlow(listOf(TaskGroup(id = 7, titleEn = "Work")))
    private val tasks = MutableStateFlow(listOf(
        Task(id = 1, title = "Read", groupId = 0),
        Task(id = 2, title = "Read report", groupId = 7),
        Task(id = 3, title = "Read book", groupId = 0, done = true),
    ))

    @BeforeTest fun setUp() { Dispatchers.setMain(StandardTestDispatcher()) }
    @AfterTest fun tearDown() { store.clear(); Dispatchers.resetMain() }

    @Test fun noGroupFiltersTasksAndAllRestoresEveryTask() = runTest {
        val model = model()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        advanceTimeBy(300); runCurrent()
        model.onAction(TaskListAction.OnGroupSelected(0L))
        runCurrent()
        assertEquals(0L, model.state.value.selectedGroup)
        assertEquals(setOf(1L, 3L), model.state.value.tasksResult.map { it.id }.toSet())

        model.onAction(TaskListAction.OnGroupSelected(7L))
        runCurrent()
        assertEquals(listOf(2L), model.state.value.tasksResult.map { it.id })
        model.onAction(TaskListAction.OnGroupSelected(null))
        runCurrent()
        assertNull(model.state.value.selectedGroup)
        assertEquals(3, model.state.value.tasksResult.size)
    }

    @Test fun noGroupRemainsSelectedWithoutAnyGroupRowsAndReceivesNewTasks() = runTest {
        groups.value = emptyList()
        tasks.value = emptyList()
        val model = model()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        model.onAction(TaskListAction.OnGroupSelected(0L))
        advanceTimeBy(300); runCurrent()
        assertEquals(0L, model.state.value.selectedGroup)
        assertTrue(model.state.value.tasksResult.isEmpty())

        tasks.value = listOf(Task(id = 4, title = "New", groupId = 0))
        runCurrent()
        assertEquals(0L, model.state.value.selectedGroup)
        assertEquals(listOf(4L), model.state.value.tasksResult.map { it.id })
    }

    @Test fun noGroupCombinesWithSearchAndCompletionFilters() = runTest {
        val model = model()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        model.onAction(TaskListAction.OnGroupSelected(0L))
        model.onAction(TaskListAction.OnSearchQueryChange("Read"))
        model.onAction(TaskListAction.OnFilterIcon)
        model.onAction(TaskListAction.OnStatusFilterChanged(Enums.DONE))
        model.onAction(TaskListAction.OnApplyFilter)
        advanceTimeBy(300); runCurrent()
        assertEquals(0L, model.state.value.selectedGroup)
        assertEquals(listOf(3L), model.state.value.tasksResult.map { it.id })
    }

    @Test fun deletingARealSelectedGroupStillFallsBackToAll() = runTest {
        val model = model()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.state.collect() }
        model.onAction(TaskListAction.OnGroupSelected(7L))
        advanceTimeBy(300); runCurrent()
        assertEquals(7L, model.state.value.selectedGroup)
        groups.value = emptyList()
        runCurrent()
        assertNull(model.state.value.selectedGroup)
        assertEquals(3, model.state.value.tasksResult.size)
    }

    private fun model(): TaskListViewModel {
        val language = LanguageProvider(UserPreferencesManager(object : UserPreferencesRepository {
            override val userPreferences = flowOf(UserPreferences())
            override suspend fun updateTheme(theme: AppTheme) = Unit
            override suspend fun updateLanguage(language: AppLanguage) = Unit
            override suspend fun updateNotifications(enabled: Boolean) = Unit
        }))
        val repository = object : TaskRepository {
            override fun getAllTasks() = tasks
            override fun getTasksByFilter(done: Boolean?, priority: List<Int>) = tasks
            override suspend fun clearGroup(id: Long) = error("Unexpected write")
            override suspend fun deleteTask(id: Long) = error("Unexpected write")
            override suspend fun upsertTask(task: Task): Long = error("Unexpected write")
            override suspend fun getTaskById(id: Long) = tasks.value.find { it.id == id }
        }
        val groupRepository = object : TaskGroupRepository {
            override fun getAllGroups() = groups
            override suspend fun addGroup(group: TaskGroup): Long = error("Unexpected write")
            override suspend fun deleteGroup(id: Long) = error("Unexpected write")
            override suspend fun getGroupById(id: Long) = groups.value.find { it.id == id }
        }
        val scheduler = object : ReminderScheduler {
            override suspend fun scheduleReminder(reminder: Reminder) = error("Unexpected scheduling")
            override suspend fun cancelReminder(taskId: Long) = error("Unexpected scheduling")
        }
        return TaskListViewModel(language, repository, groupRepository, TaskMutations(repository, scheduler))
            .also { store.put("list", it) }
    }
}
