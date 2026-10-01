package com.sepideh.lilo.task.presentation.model

data class TaskFilterOption(
    val taskStatus: List<Enums> = emptyList(),
    val priorityList: List<Priority> = emptyList()
)