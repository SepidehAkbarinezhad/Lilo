package com.sepideh.lilo.task.presentation.task_detail

data class ReminderModel(
    val hour: Int? = null,
    val minute: Int? = null,
    val initDay: Long? = null,
    val finishDay: Long? = null
)
