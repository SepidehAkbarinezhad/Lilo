package com.sepideh.lilo.task.data.local.room

import com.sepideh.lilo.task.domain.model.Task

fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    description = description,
    done = done,
    category = category,
    priority = priority,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    reminderStartDate = reminderStartDate,
    reminderEndDate = reminderEndDate,
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
    priority = priority,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
    reminderStartDate = reminderStartDate,
    reminderEndDate = reminderEndDate,
    createdAt = createdAt,
    updatedAt = updatedAt,
    completedAt = completedAt,
)