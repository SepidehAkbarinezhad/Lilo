package com.sepideh.lilo.task.data.mapper

import com.sepideh.lilo.task.data.local.room.TaskEntity
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.reminder.RepeatRule

fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    description = description,
    done = done,
    category = category,
    priority = priority.toTaskPriority().toStorageCode(),
    reminderAt = reminderAt,
    repeatRule = RepeatRule.fromCode(repeatRule),
    reminderTimeZoneId = reminderTimeZoneId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    completedAt = completedAt,
)

fun List<TaskEntity>.toTaskList() = this.map { it.toTask() }

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    done = done,
    category = category,
    priority = priority.toTaskPriority().toStorageCode(),
    reminderAt = reminderAt,
    repeatRule = repeatRule.name,
    reminderTimeZoneId = reminderTimeZoneId,
    createdAt = createdAt,
    updatedAt = updatedAt,
    completedAt = completedAt,
)
