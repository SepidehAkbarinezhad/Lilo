package com.sepideh.lilo.task.data

import com.sepideh.lilo.task.domain.reminder.ReminderScheduler

class IosReminderScheduler : ReminderScheduler {
    override fun scheduleReminder(reminder: Reminder) {
        // iOS-specific reminder scheduling
    }

    override fun cancelReminder(id: String) {
        // iOS-specific reminder canceling
    }
}
