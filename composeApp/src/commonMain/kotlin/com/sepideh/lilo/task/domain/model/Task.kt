package com.sepideh.lilo.task.domain.model

data class Task(
    val id: Long = 0 ,
    val title: String = "",
    val description: String = "",
    val done : Boolean = false,
    val category : Long = 0,
    val priority : Int = 0,
    val photo :ByteArray? = null,
)
