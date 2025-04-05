package com.sepideh.lilo.task.data

import com.sepideh.lilo.task.domain.ReminderScheduler


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class ReminderManager : ReminderScheduler{
    override fun scheduleReminder(reminder: Reminder)
    override fun cancelReminder(id: String)
}
