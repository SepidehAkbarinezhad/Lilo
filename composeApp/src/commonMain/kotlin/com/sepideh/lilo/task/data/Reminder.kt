package com.sepideh.lilo.task.data

data class Reminder(
    val id: Int,
    val title: String,
    val content: String,
    val timeInMillis: Long
)
