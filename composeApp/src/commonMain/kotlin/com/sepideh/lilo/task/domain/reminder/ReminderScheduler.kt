package com.sepideh.lilo.task.domain.reminder

interface ReminderScheduler {
    suspend fun scheduleReminder(reminder: Reminder)
    suspend fun cancelReminder(taskId: Long)
    suspend fun synchronize(reminders: List<Reminder>) {
        reminders.forEach { scheduleReminder(it) }
    }
}
