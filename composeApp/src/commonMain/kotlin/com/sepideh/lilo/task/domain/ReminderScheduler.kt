package com.sepideh.lilo.task.domain

import com.sepideh.lilo.task.data.Reminder

interface ReminderScheduler {
    fun scheduleReminder(reminder: Reminder)
    fun cancelReminder(id: String)
}

