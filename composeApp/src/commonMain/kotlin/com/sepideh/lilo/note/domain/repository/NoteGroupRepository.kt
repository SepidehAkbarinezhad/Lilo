package com.sepideh.lilo.note.domain.repository

import com.sepideh.lilo.note.domain.model.NoteGroup
import kotlinx.coroutines.flow.Flow

interface NoteGroupRepository {
    fun getAllGroups(): Flow<List<NoteGroup>>
    suspend fun getGroupById(id: Long): NoteGroup?
    suspend fun upsertGroup(group: NoteGroup): Long
    /** Atomically removes the group and leaves its notes ungrouped. */
    suspend fun deleteGroup(id: Long)
}
