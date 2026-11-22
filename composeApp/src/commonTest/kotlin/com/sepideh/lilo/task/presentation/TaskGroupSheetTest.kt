package com.sepideh.lilo.task.presentation

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.core.domain.validation.DuplicateNameException
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.*
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.repository.*
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import com.sepideh.lilo.task.presentation.detail.*
import com.sepideh.lilo.task.domain.*
import com.sepideh.lilo.task.domain.reminder.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class TaskGroupSheetTest {
    private val store = ViewModelStore()
    @BeforeTest fun setup() { Dispatchers.setMain(StandardTestDispatcher()) }
    @AfterTest fun cleanup() { store.clear(); Dispatchers.resetMain() }

    @Test fun addFailureKeepsManagementAndSuccessfulRetrySelectsAndCloses() = runTest {
        val groups = Groups()
        val model = model(groups)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.stateValue.collect() }
        model.onAction(TaskDetailAction.OnGetSelectedTaskInfo(1))
        runCurrent()
        model.onAction(TaskDetailAction.OnGroupIcon)
        model.onAction(TaskDetailAction.OnManageGroups)
        groups.failure = DuplicateNameException()
        model.onAction(TaskDetailAction.OnAddNewGroup("New"))
        runCurrent()
        assertTrue(model.stateValue.value.duplicateGroupName)
        assertTrue(model.stateValue.value.groupManagementOpen)
        assertTrue(model.stateValue.value.groupDialogOpen)
        assertEquals(7L, model.stateValue.value.task.groupId)
        groups.failure = null
        model.onAction(TaskDetailAction.OnAddNewGroup("New"))
        runCurrent()
        assertEquals(100L, model.stateValue.value.task.groupId)
        assertEquals(100L, model.stateValue.value.draftGroupId)
        assertFalse(model.stateValue.value.groupDialogOpen)
        assertFalse(model.stateValue.value.groupManagementOpen)
        model.onAction(TaskDetailAction.OnGroupIcon)
        runCurrent()
        assertEquals(100L, model.stateValue.value.draftGroupId)
        assertFalse(model.stateValue.value.groupManagementOpen)
    }

    @Test fun renameSelectedOrOtherGroupRetainsSelectionAndManagement() = runTest {
        val groups = Groups()
        val model = model(groups)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.stateValue.collect() }
        model.onAction(TaskDetailAction.OnGetSelectedTaskInfo(1))
        runCurrent()
        model.onAction(TaskDetailAction.OnGroupIcon)
        model.onAction(TaskDetailAction.OnManageGroups)
        for (id in listOf(7L, 42L)) {
            model.onAction(TaskDetailAction.OnRenameGroup(id, "ZZZ renamed"))
            runCurrent()
            assertTrue(model.stateValue.value.groupDialogOpen)
            assertTrue(model.stateValue.value.groupManagementOpen)
            assertEquals(7L, model.stateValue.value.task.groupId)
            assertEquals(7L, model.stateValue.value.draftGroupId)
            val saved = groups.getGroupById(id)!!
            assertTrue(saved.titleEn == "ZZZ renamed" || saved.titleFa == "ZZZ renamed")
        }
    }

    @Test fun deletionKeepsManagementAndClearsOnlyDeletedSelection() = runTest {
        val groups = Groups()
        val model = model(groups)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.stateValue.collect() }
        model.onAction(TaskDetailAction.OnGetSelectedTaskInfo(1))
        runCurrent()
        model.onAction(TaskDetailAction.OnGroupIcon)
        model.onAction(TaskDetailAction.OnManageGroups)
        model.onAction(TaskDetailAction.OnDeleteGroup(42))
        runCurrent()
        assertEquals(7L, model.stateValue.value.task.groupId)
        assertEquals(7L, model.stateValue.value.draftGroupId)
        model.onAction(TaskDetailAction.OnDeleteGroup(7))
        runCurrent()
        assertEquals(0L, model.stateValue.value.task.groupId)
        assertNull(model.stateValue.value.draftGroupId)
        assertTrue(model.stateValue.value.groupManagementOpen)
        assertTrue(model.stateValue.value.groupDialogOpen)
    }

    @Test fun closeAndDismissExitEntireSheetWithoutConfirmingUncommittedSelection() = runTest {
        val model = model(Groups())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.stateValue.collect() }
        model.onAction(TaskDetailAction.OnGetSelectedTaskInfo(1))
        runCurrent()
        for (close in listOf(TaskDetailAction.OnCloseManageGroups, TaskDetailAction.OnDismissGroupDialog)) {
            model.onAction(TaskDetailAction.OnGroupIcon)
            model.onAction(TaskDetailAction.OnGroupDraftSelected(42))
            model.onAction(TaskDetailAction.OnManageGroups)
            model.onAction(close)
            runCurrent()
            assertFalse(model.stateValue.value.groupDialogOpen)
            assertFalse(model.stateValue.value.groupManagementOpen)
            assertEquals(7L, model.stateValue.value.task.groupId)
        }
        model.onAction(TaskDetailAction.OnGroupIcon)
        runCurrent()
        assertEquals(7L, model.stateValue.value.draftGroupId)
    }

    @Test fun pendingDeleteBlocksCloseAndFailureAllowsRetry() = runTest {
        val groups = Groups()
        val model = model(groups)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { model.stateValue.collect() }
        model.onAction(TaskDetailAction.OnGetSelectedTaskInfo(1))
        runCurrent()
        model.onAction(TaskDetailAction.OnGroupIcon)
        model.onAction(TaskDetailAction.OnManageGroups)
        groups.gate = CompletableDeferred()
        groups.failure = IllegalStateException("Storage failure")
        model.onAction(TaskDetailAction.OnDeleteGroup(7))
        runCurrent()
        model.onAction(TaskDetailAction.OnCloseManageGroups)
        model.onAction(TaskDetailAction.OnDismissGroupDialog)
        runCurrent()
        assertTrue(model.stateValue.value.isAddingGroup)
        assertTrue(model.stateValue.value.groupManagementOpen)
        assertTrue(model.stateValue.value.groupDialogOpen)
        groups.gate!!.complete(Unit)
        runCurrent()
        assertFalse(model.stateValue.value.isAddingGroup)
        assertTrue(model.stateValue.value.hasError)
        assertEquals(7L, model.stateValue.value.task.groupId)
        groups.failure = null
        model.onAction(TaskDetailAction.OnDeleteGroup(7))
        runCurrent()
        assertEquals(0L, model.stateValue.value.task.groupId)
        assertFalse(model.stateValue.value.hasError)
        assertTrue(model.stateValue.value.groupManagementOpen)
    }

    private fun model(groups: Groups): TaskDetailViewModel {
        val language = LanguageProvider(UserPreferencesManager(object : UserPreferencesRepository {
            override val userPreferences = flowOf(UserPreferences())
            override suspend fun updateTheme(theme: AppTheme) = Unit
            override suspend fun updateLanguage(language: AppLanguage) = Unit
            override suspend fun updateNotifications(enabled: Boolean) = Unit
        }))
        val repository = object : TaskRepository {
            override fun getAllTasks() = flowOf(emptyList<Task>())
            override fun getTasksByFilter(done: Boolean?, priority: List<Int>) = getAllTasks()
            override suspend fun getTaskById(id: Long) = Task(id = id, title = "Original", groupId = 7)
            override suspend fun upsertTask(task: Task): Long = error("Sheet must not save task")
            override suspend fun deleteTask(id: Long) = Unit
            override suspend fun clearGroup(id: Long) = Unit
        }
        val scheduler = object : ReminderScheduler {
            override suspend fun scheduleReminder(reminder: Reminder) = Unit
            override suspend fun cancelReminder(taskId: Long) = Unit
        }
        val permissions = object : ReminderPermissions {
            override suspend fun missingPermission(): ReminderPermission? = null
            override suspend fun request(permission: ReminderPermission) = Unit
        }
        return TaskDetailViewModel(TaskGroupFactory(language), language, repository, groups,
            TaskMutations(repository, scheduler), permissions).also { store.put("editor", it) }
    }

    private class Groups : TaskGroupRepository {
        private val rows = MutableStateFlow(listOf(TaskGroup(id = 7, titleEn = "Original", titleFa = "Original"),
            TaskGroup(id = 42, titleEn = "Other", titleFa = "Other")))
        var failure: Exception? = null
        var gate: CompletableDeferred<Unit>? = null
        override fun getAllGroups() = rows.asStateFlow()
        override suspend fun getGroupById(id: Long) = rows.value.firstOrNull { it.id == id }
        override suspend fun addGroup(group: TaskGroup): Long {
            failure?.let { throw it }
            val saved = group.copy(id = group.id.takeIf { it != 0L } ?: 100)
            rows.value = (rows.value.filterNot { it.id == saved.id } + saved).sortedBy { it.titleFa }
            return saved.id
        }
        override suspend fun deleteGroup(id: Long) {
            gate?.await()
            failure?.let { throw it }
            rows.value = rows.value.filterNot { it.id == id }
        }
    }
}
