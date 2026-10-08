package com.sepideh.lilo.task.domain.repository

import com.sepideh.lilo.task.domain.TaskGroup
import kotlinx.coroutines.flow.Flow

interface TaskGroupRepository {
    fun getAllGroups(): Flow<List<TaskGroup>>
    suspend fun addGroup(group: TaskGroup): Long
    suspend fun deleteGroup(id: Long)
    suspend fun getGroupById(id: Long): TaskGroup?
}