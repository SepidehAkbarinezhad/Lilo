package com.sepideh.lilo.task.data.mapper

import com.sepideh.lilo.task.domain.model.TaskPriority

// Explicit legacy database values: never persist enum ordinals.
fun Int.toTaskPriority(): TaskPriority = when (this) {
    0 -> TaskPriority.HIGH
    2 -> TaskPriority.LOW
    else -> TaskPriority.MEDIUM
}

fun TaskPriority.toStorageCode(): Int = when (this) {
    TaskPriority.HIGH -> 0
    TaskPriority.MEDIUM -> 1
    TaskPriority.LOW -> 2
}
