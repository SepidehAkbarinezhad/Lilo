package com.sepideh.lilo.task.data

data class Reminder(
    val id: String,
    val title: String,
    val content: String,
    val timeInMillis: Long
)
