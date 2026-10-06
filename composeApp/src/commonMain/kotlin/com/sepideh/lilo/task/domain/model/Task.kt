package com.sepideh.lilo.task.domain.model

import com.sepideh.lilo.task.domain.reminder.RepeatRule

data class Task(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val done: Boolean = false,
    val category: Long = 0,
    val priority: Int = 0,
    val reminderAt: Long? = null,
    val repeatRule: RepeatRule = RepeatRule.NONE,
    val reminderTimeZoneId: String? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val completedAt: Long? = null,
    val imageNames: List<String> = emptyList(),
)
