package com.sepideh.lilo.task.presentation.reminder

data class ReminderModel(
    val hour: Int? = null,
    val minute: Int? = null,
    val startDay: Long? = null,
    val endDay: Long? = null
)