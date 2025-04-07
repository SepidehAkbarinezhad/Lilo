package com.sepideh.lilo.task.domain.reminder

import com.sepideh.lilo.task.data.Reminder

interface ReminderScheduler {
    fun scheduleReminder(reminder: Reminder)
    fun cancelReminder(reminder: Reminder)
}



