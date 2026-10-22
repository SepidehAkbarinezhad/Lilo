package com.sepideh.lilo.task.domain.usecase

import com.sepideh.lilo.core.domain.images.ImageStore
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.asReminder
import com.sepideh.lilo.task.domain.reminder.nextOccurrence
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.repository.TaskRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
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
    private val imageStore: ImageStore? = null,
    private val notificationsEnabled: suspend () -> Boolean = { true },
) {
    private val mutex = Mutex()

    suspend fun save(task: Task, scheduleReminder: Boolean = true,
        selectedImages: List<com.sepideh.lilo.core.domain.images.ImageSource>? = null): SaveTaskResult = mutex.withLock {
        require(task.title.isNotBlank())
        if (task.reminderAt != null) TimeZone.of(requireNotNull(task.reminderTimeZoneId))
        val previous = task.id?.let { requireNotNull(repository.getTaskById(it)) { "Task no longer exists" } }
        val imported = selectedImages?.let { requireNotNull(imageStore).importImages(it) }.orEmpty()
        // Once persistence starts, finish the commit and its side effects even if the form closes.
        // Cancellation at the database return boundary must not delete committed attachments.
        withContext(NonCancellable) {
            val saved = task.copy(imageNames = task.imageNames + imported)
            val result = try { repository.upsertTask(saved) }
            catch (failure: Throwable) {
                runCatching { imageStore?.removeImages(imported) }
                throw failure
            }
            val id = if (result == -1L) requireNotNull(task.id) else result
            // Cleanup only follows a committed row, independently of reminder scheduling.
            runCatching { imageStore?.removeImages(previous?.imageNames.orEmpty() - saved.imageNames.toSet()) }
            val reminderFailed = try {
                previous?.id?.let { scheduler.cancelReminder(it) }
                if (!scheduleReminder) {
                    scheduler.cancelReminder(id)
                } else syncReminder(saved.copy(id = id))
                false
            } catch (e: kotlinx.coroutines.CancellationException) { throw e }
            catch (_: Exception) { true }
            SaveTaskResult(id, reminderFailed, saved.imageNames)
        }
    }

    suspend fun setCompleted(id: Long, completed: Boolean) = mutex.withLock {
        // Read the current row rather than overwriting it with a stale list snapshot.
        repository.getTaskById(id)?.let { current ->
            val updated = current.copy(done = completed)
            withContext(NonCancellable) {
                repository.upsertTask(updated)
                syncReminder(updated)
            }
        }
    }

    suspend fun delete(id: Long) = mutex.withLock {
        repository.getTaskById(id)?.let { task ->
            withContext(NonCancellable) {
                // Persist first: receivers reject deleted rows even if cancelling an alarm fails.
                repository.deleteTask(id)
                runCatching { imageStore?.removeImages(task.imageNames) }
                scheduler.cancelReminder(id)
                synchronizeAll()
            }
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
        } catch (cancelled: kotlinx.coroutines.CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            if (notificationsEnabled()) notify(task)
            throw failure
        }
        if (notificationsEnabled()) notify(task)
    }

    /** Preference changes cannot race with task edits or reminder delivery. */
    suspend fun updateNotificationPreference(persist: suspend () -> Unit) = mutex.withLock {
        persist()
        scheduler.refreshNotificationPreference { activeReminders() }
    }

    private suspend fun syncReminder(task: Task) {
        scheduler.cancelReminder(requireNotNull(task.id))
        synchronizeAll()
    }

    private suspend fun activeReminders() =
        repository.getAllTasks().first().filter { !it.done }.mapNotNull { it.asReminder() }

    private suspend fun synchronizeAll() {
        scheduler.synchronize(activeReminders())
    }
}

data class SaveTaskResult(val id: Long, val reminderFailed: Boolean, val imageNames: List<String> = emptyList())
