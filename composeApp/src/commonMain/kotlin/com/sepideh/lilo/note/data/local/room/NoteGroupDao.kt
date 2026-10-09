package com.sepideh.lilo.note.data.local.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteGroupDao {
    @Query("SELECT * FROM note_groups")
    fun getAllGroups(): Flow<List<NoteGroupEntity>>
    @Query("SELECT * FROM note_groups WHERE id = :id")
    suspend fun getGroupById(id: Long): NoteGroupEntity?
    @Upsert suspend fun upsert(group: NoteGroupEntity): Long
    @Query("UPDATE notes SET groupId = NULL WHERE groupId = :id")
    suspend fun clearGroup(id: Long)
    @Query("DELETE FROM note_groups WHERE id = :id")
    suspend fun deleteById(id: Long)
    @Transaction
    suspend fun deleteGroup(id: Long) {
        clearGroup(id)
        deleteById(id)
    }
}
