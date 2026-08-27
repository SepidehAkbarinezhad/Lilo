package com.sepideh.lilo.task.domain.repository

import com.sepideh.lilo.task.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByFilter(
        done: Boolean?,
        priority: List<Int>
    ): Flow<List<Task>>
    suspend fun deleteTask(id: Long)
    suspend fun upsertTask(task: Task): Long
    suspend fun getTaskById(id: Long): Task?
}