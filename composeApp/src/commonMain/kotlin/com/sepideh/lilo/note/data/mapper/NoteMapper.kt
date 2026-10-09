package com.sepideh.lilo.note.data.mapper

import com.sepideh.lilo.note.data.local.room.NoteEntity
import com.sepideh.lilo.note.data.local.room.NoteGroupEntity
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.model.NoteGroup
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun NoteEntity.toDomain() = Note(id, title, content, groupId, createdAt, updatedAt, Json.decodeFromString<List<String>>(imageNames))
fun Note.toEntity() = NoteEntity(id, title, content, groupId, createdAt, updatedAt, Json.encodeToString(imageNames))
fun NoteGroupEntity.toDomain() = NoteGroup(id, titleEn, titleFa, isDefault)
fun NoteGroup.toEntity() = NoteGroupEntity(id, titleEn, titleFa, isDefault)
