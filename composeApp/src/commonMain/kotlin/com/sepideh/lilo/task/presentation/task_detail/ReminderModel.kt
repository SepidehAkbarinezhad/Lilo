package com.sepideh.lilo.task.presentation.task_detail

data class ReminderModel(
    val hour: Int? = null,
    val minute: Int? = null,
    val startDay: Long? = null,
    val endDay: Long? = null
)
