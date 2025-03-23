package com.sepideh.lilo.task.data

import com.sepideh.lilo.task.domain.Task

fun TaskEntity.toTask(): Task = Task(
    id = id,
    title = title,
    description = description,
    done = done,
    category = category,
    priority= priority
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
    priority= priority
)