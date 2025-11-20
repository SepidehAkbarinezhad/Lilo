package com.sepideh.lilo.task.data.local.room

import com.sepideh.lilo.task.domain.model.Task

fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    description = description,
    done = done,
    category = category,
    priority = priority,
    hour = hour,
    minute = minute,
    startDate = startDate,
    endDate = endDate
)

fun List<TaskEntity>.toTaskList() = this.map {
    it.toTask()
}

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    done = done,
    category = category,
    priority = priority,
    hour = hour,
    minute = minute,
    startDate = startDate,
    endDate = endDate
)