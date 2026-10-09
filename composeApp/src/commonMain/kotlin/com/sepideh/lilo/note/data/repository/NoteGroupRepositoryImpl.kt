package com.sepideh.lilo.note.data.repository

import com.sepideh.lilo.note.data.local.room.NoteGroupDao
import com.sepideh.lilo.note.data.mapper.toDomain
import com.sepideh.lilo.note.data.mapper.toEntity
import com.sepideh.lilo.note.domain.model.NoteGroup
import com.sepideh.lilo.note.domain.repository.NoteGroupRepository
import kotlinx.coroutines.flow.map

class NoteGroupRepositoryImpl(private val dao: NoteGroupDao) : NoteGroupRepository {
    override fun getAllGroups() = dao.getAllGroups().map { groups -> groups.map { it.toDomain() } }
    override suspend fun getGroupById(id: Long) = dao.getGroupById(id)?.toDomain()
    override suspend fun upsertGroup(group: NoteGroup): Long {
        val id = dao.upsert(group.toEntity())
        return if (group.id == 0L) id else group.id
    }
    override suspend fun deleteGroup(id: Long) = dao.deleteGroup(id)
}
