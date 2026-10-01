package com.sepideh.lilo.task.data.repository

import com.sepideh.lilo.task.data.local.room.TaskDao
import com.sepideh.lilo.task.data.mapper.toEntity
import com.sepideh.lilo.task.data.mapper.toTask
import com.sepideh.lilo.task.data.mapper.toTaskList
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.model.withTimestamps
import com.sepideh.lilo.task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@OptIn(kotlin.time.ExperimentalTime::class)
class TaskRepoImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
            .map { entities ->
                entities.toTaskList()
            }
    }

    override fun getTasksByFilter(
        done: Boolean?,
        priority: List<Int>
    ): Flow<List<Task>> {
        return taskDao.getTaskByFilter(
            done = done,
            priority = priority
        ).map {
            it.toTaskList()
        }
    }

    override suspend fun clearGroup(id: Long) = taskDao.clearGroup(id)

    override suspend fun deleteTask(id: Long) {
        taskDao.deleteById(id)
    }

    override suspend fun upsertTask(task: Task): Long {
        val existing = task.id?.let { taskDao.getTaskById(it) }
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        return taskDao.upsert(task.withTimestamps(existing?.toTask(), now).toEntity())
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)?.toTask()
    }
}