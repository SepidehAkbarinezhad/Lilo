package com.sepideh.lilo.task.domain.usecase

import com.sepideh.lilo.core.domain.images.ImageStore
import com.sepideh.lilo.core.domain.images.ImageSource
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

/** Orders task commits with reminder validation, delivery and scheduling, independently of screens. */
@OptIn(ExperimentalTime::class)
class TaskMutations(
    private val repository: TaskRepository,
    private val scheduler: ReminderScheduler,
    private val imageStore: ImageStore? = null,
    private val notificationsEnabled: suspend () -> Boolean = { true },
) {
    // Room transactions alone cannot order a read/modify/write with OS notification side effects.
    // Whole-list reconciliation and the notification preference also cross task boundaries.
    // File imports and cleanup do not participate in this critical section.
    private val mutex = Mutex()

    suspend fun save(task: Task, scheduleReminder: Boolean = true,
        selectedImages: List<ImageSource>? = null): SaveTaskResult {
        require(task.title.isNotBlank())
        if (task.reminderAt != null) TimeZone.of(requireNotNull(task.reminderTimeZoneId))
        // Avoid importing for an already-deleted row. This is only a preflight check;
        // completion, deletion and other saves may proceed while the provider is copying.
        task.id?.let { requireNotNull(repository.getTaskById(it)) { "Task no longer exists" } }
        val imported = selectedImages?.let { requireNotNull(imageStore).importImages(it) }.orEmpty()
        var committed = false
        var obsoleteImages = emptyList<String>()
        try {
            return mutex.withLock {
                val previous = task.id?.let { requireNotNull(repository.getTaskById(it)) { "Task no longer exists" } }
                // Once persistence starts, finish its side effects even if the caller closes.
                // Mark ownership inside this context, before cancellation can discard the result.
                withContext(NonCancellable) {
                    val retainedImages = if (previous == null) task.imageNames else
                        task.imageNames.filter { it in previous.imageNames }
                    val saved = task.copy(done = previous?.done ?: task.done, imageNames = retainedImages + imported)
                    val result = repository.upsertTask(saved)
                    committed = true
                    obsoleteImages = previous?.imageNames.orEmpty() - saved.imageNames.toSet()
                    val id = if (result == -1L) requireNotNull(task.id) else result
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
        } finally {
            // Includes cancellation while waiting for the lock and deletion during import.
            // A stale draft cannot reattach obsolete names: retainedImages is checked above.
            removeImagesBestEffort(if (committed) obsoleteImages else imported)
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

    suspend fun delete(id: Long) {
        var obsoleteImages = emptyList<String>()
        try {
            mutex.withLock {
                repository.getTaskById(id)?.let { task ->
                    withContext(NonCancellable) {
                        // Persist first: receivers reject deleted rows even if cancelling an alarm fails.
                        repository.deleteTask(id)
                        obsoleteImages = task.imageNames
                        scheduler.cancelReminder(id)
                        synchronizeAll()
                    }
                }
            }
        } finally {
            removeImagesBestEffort(obsoleteImages)
        }
    }

    private suspend fun removeImagesBestEffort(names: List<String>) {
        if (names.isEmpty()) return
        withContext(NonCancellable) {
            runCatching { imageStore?.removeImages(names) }
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
