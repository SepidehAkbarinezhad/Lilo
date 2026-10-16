package com.sepideh.lilo.settings

import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.UpdateReminderNotifications
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.domain.reminder.*
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class ReminderNotificationsTest {
    private val at = 2000000000000L
    @Test fun mutedOccurrenceDoesNotNotifyButRearmsAndKeepsTask() = runTest {
        val repo = Tasks(task())
        val scheduler = Scheduler()
        var enabled = false
        var delivered = 0
        val mutations = TaskMutations(repo, scheduler, notificationsEnabled = { enabled })
        mutations.deliverReminder(1, at) { delivered++ }
        assertEquals(0, delivered)
        assertEquals(1, scheduler.scheduled.size)
        assertEquals(task(), repo.task)
        enabled = true
        mutations.deliverReminder(1, at) { delivered++ }
        assertEquals(1, delivered)
    }
    @Test fun preferenceToggleSynchronizesWithoutChangingTaskOrReminder() = runTest {
        val repo = Tasks(task())
        val prefs = Preferences()
        val scheduler = Scheduler()
        val update = UpdateReminderNotifications(prefs, TaskMutations(repo, scheduler))
        update(false)
        assertFalse(prefs.userPreferences.value.notificationsEnabled)
        assertEquals(listOf(task().asReminder()), scheduler.synchronized)
        assertEquals(task(), repo.task)
        update(true)
        assertTrue(prefs.userPreferences.value.notificationsEnabled)
        assertEquals(task(), repo.task)
    }
    @Test fun schedulerFailureStillRespectsMute() = runTest {
        for (enabled in listOf(false, true)) {
            var delivered = 0
            val mutations = TaskMutations(Tasks(task()), Scheduler(failure = IllegalStateException()), notificationsEnabled = { enabled })
            assertFailsWith<IllegalStateException> { mutations.deliverReminder(1, at) { delivered++ } }
            assertEquals(if (enabled) 1 else 0, delivered)
        }
    }
    @Test fun cancellationNeverDelivers() = runTest {
        var delivered = false
        val mutations = TaskMutations(Tasks(task()), Scheduler(failure = CancellationException()))
        assertFailsWith<CancellationException> { mutations.deliverReminder(1, at) { delivered = true } }
        assertFalse(delivered)
    }
    @Test fun completedAndStaleOccurrencesDoNotNotify() = runTest {
        var delivered = false
        val repo = Tasks(task().copy(done = true))
        val mutations = TaskMutations(repo, Scheduler())
        mutations.deliverReminder(1, at) { delivered = true }
        repo.task = task()
        mutations.deliverReminder(1, at + 1000) { delivered = true }
        assertFalse(delivered)
    }
    @Test fun completionCancelsRecurringReminderAndPreservesLatestTaskFields() = runTest {
        val current = task().copy(title = "Edited after notification", description = "Keep", groupId = 7,
            imageNames = listOf("keep.jpg"))
        val repo = Tasks(current)
        val scheduler = Scheduler()
        val mutations = TaskMutations(repo, scheduler)
        mutations.setCompleted(1, true)
        assertEquals(current.copy(done = true), repo.task)
        assertEquals(listOf(1L), scheduler.cancelled)
        assertTrue(scheduler.synchronized.isEmpty())
        var delivered = false
        mutations.deliverReminder(1, at) { delivered = true }
        assertFalse(delivered)
        mutations.setCompleted(1, true) // Repeated action must never toggle completion back off.
        assertTrue(repo.task!!.done)
    }

    @Test fun completionOfDeletedTaskIsHarmlessAndDoesNotCreateARow() = runTest {
        val repo = Tasks()
        val scheduler = Scheduler()
        TaskMutations(repo, scheduler).setCompleted(1, true)
        assertNull(repo.task)
        assertTrue(scheduler.cancelled.isEmpty())
    }

    @Test fun completionKeepsOtherTasksRemindersScheduled() = runTest {
        val repo = object : TaskRepository {
            val tasks = mutableMapOf(1L to task(), 2L to task().copy(id = 2, title = "Other"))
            override fun getAllTasks() = flowOf(tasks.values.toList())
            override fun getTasksByFilter(done: Boolean?, priority: List<Int>) = getAllTasks()
            override suspend fun getTaskById(id: Long) = tasks[id]
            override suspend fun upsertTask(task: Task): Long { tasks[task.id!!] = task; return task.id }
            override suspend fun deleteTask(id: Long) { tasks.remove(id) }
            override suspend fun clearGroup(id: Long) = Unit
        }
        val scheduler = Scheduler()
        TaskMutations(repo, scheduler).setCompleted(1, true)
        assertEquals(listOf(2L), scheduler.synchronized.map { it.id })
        assertFalse(repo.tasks.getValue(2).done)
    }

    @Test fun failedCompletionDoesNotCancelReminderOrPretendTaskWasSaved() = runTest {
        val original = task()
        val repo = object : TaskRepository by Tasks(original) {
            override suspend fun upsertTask(task: Task): Long = error("Disk unavailable")
        }
        val scheduler = Scheduler()
        assertFailsWith<IllegalStateException> { TaskMutations(repo, scheduler).setCompleted(1, true) }
        assertEquals(original, repo.getTaskById(1))
        assertTrue(scheduler.cancelled.isEmpty())
    }

    private fun task() = Task(id = 1, title = "Remember", reminderAt = at, reminderTimeZoneId = "UTC", repeatRule = RepeatRule.DAILY)
}

internal class Preferences : UserPreferencesRepository {
    override val userPreferences = MutableStateFlow(UserPreferences())
    var fail = false
    override suspend fun updateTheme(theme: AppTheme) {
        check(!fail)
        userPreferences.update { it.copy(theme = theme) }
    }
    override suspend fun updateLanguage(language: AppLanguage) {
        check(!fail)
        userPreferences.update { it.copy(language = language) }
    }
    override suspend fun updateNotifications(enabled: Boolean) {
        check(!fail)
        userPreferences.update { it.copy(notificationsEnabled = enabled) }
    }
}
internal class Tasks(var task: Task? = null) : TaskRepository {
    override fun getAllTasks() = flowOf(listOfNotNull(task))
    override fun getTasksByFilter(done: Boolean?, priority: List<Int>) = getAllTasks()
    override suspend fun getTaskById(id: Long) = task?.takeIf { it.id == id }
    override suspend fun clearGroup(id: Long) = Unit
    override suspend fun deleteTask(id: Long) { task = null }
    override suspend fun upsertTask(task: Task): Long { this.task = task; return task.id ?: 1 }
}
internal class Scheduler(private val failure: Exception? = null) : ReminderScheduler {
    val scheduled = mutableListOf<Reminder>()
    val cancelled = mutableListOf<Long>()
    var synchronized = emptyList<Reminder>()
    override suspend fun scheduleReminder(reminder: Reminder) {
        failure?.let { throw it }
        scheduled += reminder
    }
    override suspend fun synchronize(reminders: List<Reminder>) { synchronized = reminders }
    override suspend fun refreshNotificationPreference(reminders: suspend () -> List<Reminder>) { synchronize(reminders()) }
    override suspend fun cancelReminder(taskId: Long) { cancelled += taskId }
}
