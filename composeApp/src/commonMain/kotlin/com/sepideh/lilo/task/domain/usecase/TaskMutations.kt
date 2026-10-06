package com.sepideh.lilo.task.domain.usecase

import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.asReminder
import com.sepideh.lilo.task.domain.reminder.nextOccurrence
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime

/** Serializes mutations and owns notification side effects independently of either screen. */
@OptIn(ExperimentalTime::class)
class TaskMutations(
    private val repository: TaskRepository,
    private val scheduler: ReminderScheduler,
    private val imageStore: com.sepideh.lilo.core.domain.images.ImageStore? = null,
) {
    private val mutex = Mutex()

    suspend fun save(task: Task, scheduleReminder: Boolean = true): SaveTaskResult = mutex.withLock {
        require(task.title.isNotBlank())
        if (task.reminderAt != null) TimeZone.of(requireNotNull(task.reminderTimeZoneId))
        val previous = task.id?.let { repository.getTaskById(it) }
        val result = repository.upsertTask(task)
        val id = if (result == -1L) requireNotNull(task.id) else result
        val reminderFailed = try {
            previous?.id?.let { scheduler.cancelReminder(it) }
            if (!scheduleReminder) {
                scheduler.cancelReminder(id)
            } else syncReminder(task.copy(id = id))
            false
        } catch (e: kotlinx.coroutines.CancellationException) { throw e }
        catch (_: Exception) { true }
        SaveTaskResult(id, reminderFailed)
    }

    suspend fun setCompleted(id: Long, completed: Boolean) = mutex.withLock {
        // Read the current row rather than overwriting it with a stale list snapshot.
        repository.getTaskById(id)?.let { current ->
            val updated = current.copy(done = completed)
            repository.upsertTask(updated)
            syncReminder(updated)
        }
    }

    suspend fun delete(id: Long) = mutex.withLock {
        repository.getTaskById(id)?.let { task ->
            scheduler.cancelReminder(id)
            repository.deleteTask(id)
            synchronizeAll()
            imageStore?.removeImages(task.imageNames)
        }
    }

    /** Rebuild schedules after reboot, permission grant or foreground entry. */
    suspend fun restoreReminders() = mutex.withLock { synchronizeAll() }

    /** Android alarm delivery checks current persistence under the same mutation lock. */
    suspend fun deliverReminder(id: Long, expectedAt: Long, notify: (Task) -> Unit) = mutex.withLock {
        val task = repository.getTaskById(id) ?: return@withLock
        val reminder = task.asReminder() ?: return@withLock
        if (task.done || reminder.nextOccurrence(expectedAt - 1) != expectedAt) return@withLock
        // Only a persisted current schedule may re-arm itself.
        try {
            scheduler.scheduleReminder(reminder)
        } finally {
            // A failure to re-arm must not suppress the occurrence already due.
            notify(task)
        }
    }

    private suspend fun syncReminder(task: Task) {
        scheduler.cancelReminder(requireNotNull(task.id))
        synchronizeAll()
    }

    private suspend fun synchronizeAll() {
        val active = repository.getAllTasks().first().filter { !it.done }.mapNotNull { it.asReminder() }
        scheduler.synchronize(active)
    }
}

data class SaveTaskResult(val id: Long, val reminderFailed: Boolean)
