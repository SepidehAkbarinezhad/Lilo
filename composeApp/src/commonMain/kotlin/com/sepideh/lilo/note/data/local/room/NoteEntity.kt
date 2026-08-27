package com.sepideh.lilo.note.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val categoryId: Long? = null,
    val createdAt: Long,
    val updatedAt: Long
)