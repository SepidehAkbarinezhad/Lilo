package com.sepideh.lilo.task.data

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
    val priority: Int = 0
)
