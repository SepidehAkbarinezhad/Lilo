package com.sepideh.lilo.task.presentation.task_detail

data class ReminderModel(
    val hour: Int? = 5,
    val minute: Int? = null,
    val startDay: Long? = null,
    val endDay: Long? = null
)
