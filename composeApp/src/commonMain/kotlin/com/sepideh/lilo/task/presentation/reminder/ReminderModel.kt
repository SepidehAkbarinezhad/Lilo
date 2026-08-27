package com.sepideh.lilo.task.presentation.reminder

data class ReminderModel(
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val reminderStartDate: Long? = null,
    val reminderEndDate: Long? = null
)