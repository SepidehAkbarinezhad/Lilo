package com.sepideh.lilo.task.data.repository

import com.sepideh.lilo.task.data.local.room.TaskGroupDao
import com.sepideh.lilo.task.data.local.room.toDomain
import com.sepideh.lilo.task.data.local.room.toDomainList
import com.sepideh.lilo.task.data.local.room.toEntity
import com.sepideh.lilo.task.domain.TaskGroup
import com.sepideh.lilo.task.domain.repository.TaskGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskGroupRepositoryImpl(
    private val taskGroupDao: TaskGroupDao,
) : TaskGroupRepository {
    override fun getAllGroups(): Flow<List<TaskGroup>> =
        taskGroupDao.getAllGroups()
            .map { it.toDomainList() }

    override suspend fun addGroup(group: TaskGroup): Long {
        return taskGroupDao.upsert(group.toEntity())
    }

    override suspend fun deleteGroup(id: Long) {
        taskGroupDao.deleteById(id)
    }

    override suspend fun getGroupById(id: Long): TaskGroup? {
        return taskGroupDao.getGroupById(id)?.toDomain()
    }
}