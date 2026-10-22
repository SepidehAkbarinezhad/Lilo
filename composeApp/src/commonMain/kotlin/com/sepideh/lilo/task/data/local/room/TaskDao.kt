package com.sepideh.lilo.task.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Upsert
    suspend fun upsert(task: TaskEntity): Long

    @Query("UPDATE TaskEntity SET groupId = NULL WHERE groupId = :id")
    suspend fun clearGroup(id: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM task_groups WHERE id = :id)")
    suspend fun groupExists(id: Long): Boolean

    /** A group may have been deleted while its form was open. Resolve it atomically. */
    @Transaction
    suspend fun upsertWithGroup(task: TaskEntity): Long {
        val groupId = task.groupId?.takeIf { groupExists(it) }
        val insertedId = upsert(task.copy(groupId = groupId))
        return if (task.id == 0L) insertedId else task.id
    }

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM TaskEntity WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM TaskEntity WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("SELECT * FROM TaskEntity WHERE (:done IS NULL OR done = :done) AND (:filterPriority = 0 OR priority IN (:priority))")
    fun getTaskByFilter(done: Boolean?, priority: List<Int>, filterPriority: Boolean = priority.isNotEmpty()): Flow<List<TaskEntity>>

    @Query("SELECT * FROM TaskEntity")
    fun getAllTasks(): Flow<List<TaskEntity>>
}