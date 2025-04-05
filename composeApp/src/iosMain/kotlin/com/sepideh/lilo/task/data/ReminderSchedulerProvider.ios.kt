package com.sepideh.lilo.task.data

import com.sepideh.lilo.task.domain.ReminderScheduler


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ReminderManager : ReminderScheduler {
    actual override fun scheduleReminder(reminder: Reminder) {
    }

    actual override fun cancelReminder(id: String) {
    }
}