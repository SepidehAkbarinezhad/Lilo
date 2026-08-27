package com.sepideh.lilo.note.data.mapper

import com.sepideh.lilo.note.data.local.room.NoteEntity
import com.sepideh.lilo.note.domain.model.Note

fun NoteEntity.toDomain() = Note(id, title, content, categoryId, createdAt, updatedAt)

fun Note.toEntity() = NoteEntity(id, title, content, categoryId, createdAt, updatedAt)