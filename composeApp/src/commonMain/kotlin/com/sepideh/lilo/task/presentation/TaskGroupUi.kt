package com.sepideh.lilo.task.presentation

data class TaskGroupUi(
    val id: Long = 0,
    val title: String = "",
    val isDeletable: Boolean = true,
    val isEditable: Boolean = true
)