package com.sepideh.lilo.task.presentation.task_detail

data class ReminderModel(
    val hour: Int? = 13,
    val minute: Int? = 25,
    val startDay: Long? = null,
    val endDay: Long? = null
)
