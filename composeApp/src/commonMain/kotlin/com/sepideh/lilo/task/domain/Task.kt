package com.sepideh.lilo.task.domain

data class Task(
    val id: Long = 0 ,
    val title: String = "",
    val description: String = "",
    val done : Boolean = false,
    val photo :ByteArray? = null,
)
