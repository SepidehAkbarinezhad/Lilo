package com.sepideh.lilo.note.domain.model


data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val categoryId: Long? = null,
    val createdAt: Long,
    val updatedAt: Long
)