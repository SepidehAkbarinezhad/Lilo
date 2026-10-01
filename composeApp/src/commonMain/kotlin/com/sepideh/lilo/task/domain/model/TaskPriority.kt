package com.sepideh.lilo.task.domain.model

/** Stable semantic IDs retained for compatibility with the existing Task and Notes APIs. */
enum class TaskPriority(val id: Int, val rank: Int) {
    HIGH(0, 0), MEDIUM(1, 1), LOW(2, 2);

    companion object {
        fun fromId(id: Int): TaskPriority = entries.firstOrNull { it.id == id } ?: MEDIUM
    }
}
