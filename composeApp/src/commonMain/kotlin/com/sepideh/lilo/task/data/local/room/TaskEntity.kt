package com.sepideh.lilo.task.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val title: String,
    val description: String,
    val done: Boolean = false,
    val category: Long = 0,
    val priority: Int = 0,
    val reminderStartDate: Long? = null,
    val reminderEndDate: Long? = null,   // null = single-day reminder, non-null = range (future version)
    val reminderHour: Int? = null,
    val reminderMinute: Int? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val completedAt: Long? = null
)