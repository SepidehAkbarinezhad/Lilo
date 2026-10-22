package com.sepideh.lilo.note.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Upsert
    suspend fun upsertNote(note: NoteEntity): Long

    @Query("SELECT EXISTS(SELECT 1 FROM note_groups WHERE id = :id)")
    suspend fun groupExists(id: Long): Boolean

    @Transaction
    suspend fun upsertWithGroup(note: NoteEntity): Long {
        val groupId = note.groupId?.takeIf { groupExists(it) }
        val insertedId = upsertNote(note.copy(groupId = groupId))
        return if (note.id == 0L) insertedId else note.id
    }

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: Long)
}