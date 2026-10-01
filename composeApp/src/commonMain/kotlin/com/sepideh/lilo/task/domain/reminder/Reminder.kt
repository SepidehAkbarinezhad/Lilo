package com.sepideh.lilo.task.domain.reminder

data class Reminder(
    val id: Int,
    val title: String,
    val content: String,
    val startDate: Long?,
    val endDate: Long?
)
