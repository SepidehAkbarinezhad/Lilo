package com.sepideh.lilo.task.domain.reminder

import kotlinx.coroutines.CancellationException

interface ReminderScheduler {
    /** Android gates delivery; platforms with OS-delivered notifications rebuild pending requests. */
    suspend fun refreshNotificationPreference(reminders: suspend () -> List<Reminder>) = Unit

    suspend fun scheduleReminder(reminder: Reminder)
    suspend fun cancelReminder(taskId: Long)
    suspend fun synchronize(reminders: List<Reminder>) {
        var firstFailure: Exception? = null
        reminders.forEach { reminder ->
            try { scheduleReminder(reminder) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (failure: Exception) {
                println("Reminder scheduling failed for task ${reminder.id}: ${failure::class.simpleName}")
                if (firstFailure == null) firstFailure = failure
                else if (firstFailure !== failure) firstFailure.addSuppressed(failure)
            }
        }
        firstFailure?.let { throw it }
    }
}
