package com.sepideh.lilo.task.presentation.model

data class TaskFilterOption(
    val taskStatus: MutableList<Enums> = mutableListOf(),
    val priorityList: MutableList<Priority> = mutableListOf()
)