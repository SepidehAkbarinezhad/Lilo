package com.sepideh.lilo.task.domain.model

fun Task.withTimestamps(previous: Task?, now: Long): Task = copy(
    title = title.trim(),
    createdAt = previous?.createdAt?.takeIf { it > 0 } ?: createdAt.takeIf { it > 0 } ?: now,
    updatedAt = now,
    completedAt = if (done) previous?.completedAt ?: now else null,
)
