package com.sepideh.lilo.note.data.repoImpl

import com.sepideh.lilo.note.data.local.room.NoteDao
import com.sepideh.lilo.note.data.mapper.toDomain
import com.sepideh.lilo.note.data.mapper.toEntity
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepoImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> =
        noteDao.getAllNotes().map { list -> list.map { it.toDomain() } }

    override suspend fun getNoteById(id: Long): Note? =
        noteDao.getNoteById(id)?.toDomain()

    override suspend fun upsertNote(note: Note): Long =
        noteDao.upsertNote(note.toEntity())

    override suspend fun deleteNote(id: Long) =
        noteDao.deleteNote(id)
}