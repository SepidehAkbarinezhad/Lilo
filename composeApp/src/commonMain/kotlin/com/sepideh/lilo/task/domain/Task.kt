package com.sepideh.lilo.task.domain

data class Task(
    val id: Long? ,
    val title: String,
    val description: String,
    val photo :ByteArray? = null
)
