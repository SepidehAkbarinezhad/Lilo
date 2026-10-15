package com.sepideh.lilo.task.domain.reminder

interface ReminderScheduler {
    /** Android gates delivery; platforms with OS-delivered notifications rebuild pending requests. */
    suspend fun refreshNotificationPreference(reminders: suspend () -> List<Reminder>) = Unit

    suspend fun scheduleReminder(reminder: Reminder)
    suspend fun cancelReminder(taskId: Long)
    suspend fun synchronize(reminders: List<Reminder>) {
        reminders.forEach { scheduleReminder(it) }
    }
}
