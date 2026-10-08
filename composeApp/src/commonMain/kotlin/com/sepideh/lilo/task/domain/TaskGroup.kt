package com.sepideh.lilo.task.domain

data class TaskGroup(
    val id: Long = 0,
    val titleEn: String = "",
    val titleFa: String = "",
    val isDefault: Boolean = false,
)
