package com.sepideh.lilo.task.data.repositoryImpl

import com.sepideh.lilo.task.data.local.room.TaskDao
import com.sepideh.lilo.task.data.local.room.toEntity
import com.sepideh.lilo.task.data.local.room.toTask
import com.sepideh.lilo.task.data.local.room.toTaskList
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    override suspend fun deleteTask(id: Long) {
        taskDao.deleteById(id)
    }

    override suspend fun upsertTask(task: Task): Long {
        return taskDao.upsert(task.toEntity())
    }

    override suspend fun getTaskById(id: Long): Task? {
        return taskDao.getTaskById(id)?.toTask()
    }
}