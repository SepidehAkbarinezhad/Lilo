package com.sepideh.lilo.task.domain.model

data class Task(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val done: Boolean = false,
    val category: Long = 0,
    val priority: Int = 0,
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val reminderStartDate: Long? = null,
    val reminderEndDate: Long? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val completedAt: Long? = null,
)