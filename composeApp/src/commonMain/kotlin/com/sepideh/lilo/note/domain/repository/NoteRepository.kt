package com.sepideh.lilo.note.domain.repository

import com.sepideh.lilo.note.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun upsertNote(note: Note): Long
    suspend fun deleteNote(id: Long)
}