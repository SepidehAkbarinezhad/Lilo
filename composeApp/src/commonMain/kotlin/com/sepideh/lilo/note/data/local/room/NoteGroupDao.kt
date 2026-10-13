package com.sepideh.lilo.note.data.local.room

import com.sepideh.lilo.core.domain.validation.requireUniqueName
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

    @Query("SELECT * FROM note_groups")
    suspend fun getGroupSnapshot(): List<NoteGroupEntity>

    @Transaction
    suspend fun upsertUnique(group: NoteGroupEntity): Long {
        val otherNames = getGroupSnapshot().filter { it.id != group.id }
            .flatMap { listOf(it.titleEn, it.titleFa) }
        listOf(group.titleEn, group.titleFa).filter { it.isNotBlank() }.forEach {
            requireUniqueName(it, otherNames)
        }
        return upsert(group)
    }
}
