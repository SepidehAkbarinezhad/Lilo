package com.sepideh.lilo.task.presentation

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.core.domain.images.ImageSource
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.*
import com.sepideh.lilo.task.domain.*
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.*
import com.sepideh.lilo.task.domain.repository.*
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import com.sepideh.lilo.task.presentation.detail.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TaskDiscardTest {
    private val store = ViewModelStore()
    @BeforeTest fun setUp() { Dispatchers.setMain(StandardTestDispatcher()) }
    @AfterTest fun tearDown() { store.clear(); Dispatchers.resetMain() }

    @Test fun unchangedTaskLeavesWithoutConfirmation() = runTest {
        val model = model()
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            model.baseOneTimeEvents.collect { events += it.navigateBack }
        }
        model.onAction(TaskDetailAction.OnGetSelectedTaskInfo(1))
        runCurrent()
        model.onAction(TaskDetailAction.Back)
        runCurrent()
        assertEquals(listOf(true), events)
        assertFalse(model.stateValue.value.confirmDiscard)
    }

    @Test fun changedTaskKeepsEditsUntilDiscardIsConfirmed() = runTest {
        val model = model()
        val events = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.stateValue.collect() }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            model.baseOneTimeEvents.collect { events += it.navigateBack }
        }
        model.onAction(TaskDetailAction.OnGetSelectedTaskInfo(1))
        runCurrent()
        model.onAction(TaskDetailAction.OnTitleChanged("Edited"))
        model.onAction(TaskDetailAction.Back)
        runCurrent()
        assertTrue(model.stateValue.value.confirmDiscard)
        assertTrue(events.isEmpty())
        model.onAction(TaskDetailAction.KeepEditing)
        runCurrent()
        assertFalse(model.stateValue.value.confirmDiscard)
        assertEquals("Edited", model.stateValue.value.task.title)
        model.onAction(TaskDetailAction.Back)
        model.onAction(TaskDetailAction.Discard)
        runCurrent()
        assertEquals(listOf(true), events)
        assertFalse(model.stateValue.value.confirmDiscard)
    }

    @Test fun addingImagesToOtherwiseEmptyFormRequiresConfirmation() = runTest {
        val model = model()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.stateValue.collect() }
        model.onAction(TaskDetailAction.OnImagesSelected(listOf(ImageSource("photo"))))
        model.onAction(TaskDetailAction.Back)
        runCurrent()
        assertTrue(model.stateValue.value.confirmDiscard)
    }

    private fun model(): TaskDetailViewModel {
        val language = LanguageProvider(UserPreferencesManager(object : UserPreferencesRepository {
            override val userPreferences = flowOf(UserPreferences())
            override suspend fun updateTheme(theme: AppTheme) {}
            override suspend fun updateLanguage(language: AppLanguage) {}
            override suspend fun updateNotifications(enabled: Boolean) {}
        }))
        val repository = object : TaskRepository {
            override fun getAllTasks() = flowOf(emptyList<Task>())
            override fun getTasksByFilter(done: Boolean?, priority: List<Int>) = getAllTasks()
            override suspend fun clearGroup(id: Long) {}
            override suspend fun deleteTask(id: Long) {}
            override suspend fun upsertTask(task: Task): Long = error("Discard must not save")
            override suspend fun getTaskById(id: Long) = Task(id = id, title = "Original", priority = 1)
        }
        val groups = object : TaskGroupRepository {
            override fun getAllGroups() = flowOf(emptyList<TaskGroup>())
            override suspend fun addGroup(group: TaskGroup): Long = error("Unexpected group write")
            override suspend fun deleteGroup(id: Long) {}
            override suspend fun getGroupById(id: Long): TaskGroup? = null
        }
        val scheduler = object : ReminderScheduler {
            override suspend fun scheduleReminder(reminder: Reminder) {}
            override suspend fun cancelReminder(taskId: Long) {}
        }
        val permissions = object : ReminderPermissions {
            override suspend fun missingPermission(): ReminderPermission? = null
            override suspend fun request(permission: ReminderPermission) {}
        }
        return TaskDetailViewModel(TaskGroupFactory(language), language, repository, groups,
            TaskMutations(repository, scheduler), permissions).also { store.put("task", it) }
    }
}
