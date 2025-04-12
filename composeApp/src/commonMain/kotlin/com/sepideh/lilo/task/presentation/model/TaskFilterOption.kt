package com.sepideh.lilo.task.presentation.model

data class TaskFilterOption(
    val taskStatus: TaskStatus = TaskStatus.ALL,
    val priority: Priority = Priority.priorities[0]
)
