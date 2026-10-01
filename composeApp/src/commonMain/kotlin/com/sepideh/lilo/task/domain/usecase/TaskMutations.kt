package com.sepideh.lilo.task.domain.usecase

import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.Reminder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.repository.TaskRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Serializes mutations and owns notification side effects independently of either screen. */
@OptIn(ExperimentalTime::class)
class TaskMutations(
    private val repository: TaskRepository,
    private val scheduler: ReminderScheduler,
) {
    private val mutex = Mutex()

    suspend fun save(task: Task, scheduleReminder: Boolean = true): SaveTaskResult = mutex.withLock {
        require(task.title.isNotBlank())
        val previous = task.id?.let { repository.getTaskById(it) }
        val result = repository.upsertTask(task)
        val id = if (result == -1L) requireNotNull(task.id) else result
        val reminderFailed = try {
            previous?.let { scheduler.cancelReminder(it.asReminder()) }
            syncReminder(task.copy(id = id), scheduleReminder)
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
            scheduler.cancelReminder(task.asReminder())
            repository.deleteTask(id)
        }
    }

    private fun syncReminder(task: Task, schedule: Boolean = true) {
        val reminder = task.asReminder()
        scheduler.cancelReminder(reminder)
        if (schedule && !task.done && reminder.startDate != null && reminder.startDate > Clock.System.now().toEpochMilliseconds()) {
            scheduler.scheduleReminder(reminder)
        }
    }

    private fun Task.asReminder() = Reminder(
        id = requireNotNull(id).toInt(), title = title, content = description,
        startDate = combine(reminderStartDate, reminderHour, reminderMinute),
        endDate = combine(reminderEndDate, reminderHour, reminderMinute),
    )

    private fun combine(day: Long?, hour: Int?, minute: Int?): Long? {
        if (day == null || hour == null || minute == null) return null
        if (hour !in 0..23 || minute !in 0..59) return null
        val zone = TimeZone.currentSystemDefault()
        val date = Instant.fromEpochMilliseconds(day).toLocalDateTime(zone).date
        return LocalDateTime(date.year, date.month, date.dayOfMonth, hour, minute).toInstant(zone).toEpochMilliseconds()
    }
}

data class SaveTaskResult(val id: Long, val reminderFailed: Boolean)
