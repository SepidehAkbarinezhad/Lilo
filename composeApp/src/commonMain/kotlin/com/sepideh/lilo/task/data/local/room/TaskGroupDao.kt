package com.sepideh.lilo.task.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskGroupDao {
    @Upsert
    suspend fun upsert(group: TaskGroupEntity): Long

    @Delete
    suspend fun delete(group: TaskGroupEntity)

    @Query("DELETE FROM task_groups WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM task_groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Long): TaskGroupEntity?

    @Query("SELECT * FROM task_groups")
    fun getAllGroups(): Flow<List<TaskGroupEntity>>
}