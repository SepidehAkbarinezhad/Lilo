package com.sepideh.lilo.task.presentation.model

data class TaskFilterOption(
    val taskStatus: MutableList<TaskStatus> = mutableListOf(),
    val priorityList: MutableList<Priority> = mutableListOf()
)
