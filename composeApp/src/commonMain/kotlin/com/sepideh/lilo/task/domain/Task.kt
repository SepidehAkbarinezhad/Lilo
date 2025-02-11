package com.sepideh.lilo.task.domain

data class Task(
    val id: String = "",
    val title: String,
    val description: String,
    val photo :ByteArray? = null
)
