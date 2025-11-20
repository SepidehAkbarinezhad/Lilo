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
    val hour: Int? = null,
    val minute: Int? = null,
    val startDate: Long?,
    val endDate: Long?
)