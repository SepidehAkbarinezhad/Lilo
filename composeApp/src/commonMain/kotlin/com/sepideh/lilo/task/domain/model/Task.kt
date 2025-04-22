package com.sepideh.lilo.task.domain.model

data class Task(
    val id: Long? = null,
    val title: String = "",
    val description: String = "",
    val done: Boolean = false,
    val category: Long = 0,
    val priority: Int = 0,
    val hour: Int? = null,
    val minute: Int? = null,
    val startDate: Long? = null,
    val endDate: Long? = null,
    val photo: ByteArray? = null,
)
