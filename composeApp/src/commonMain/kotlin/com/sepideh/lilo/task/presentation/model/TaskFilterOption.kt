package com.sepideh.lilo.task.presentation.model

data class TaskFilterOption(
    val taskStatus: TaskStatus = TaskStatus.ALL,
    val priorityList: MutableList<Priority> = Priority.priorities.toMutableList()
)
