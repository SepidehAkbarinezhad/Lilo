package com.sepideh.lilo.task.data.local.room

import com.sepideh.lilo.core.domain.validation.requireUniqueName
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("SELECT * FROM task_groups")
    suspend fun getGroupSnapshot(): List<TaskGroupEntity>

    @Transaction
    suspend fun upsertUnique(group: TaskGroupEntity): Long {
        val otherNames = getGroupSnapshot().filter { it.id != group.id }
            .flatMap { listOf(it.titleEn, it.titleFa) }
        listOf(group.titleEn, group.titleFa).filter { it.isNotBlank() }.forEach {
            requireUniqueName(it, otherNames)
        }
        return upsert(group)
    }
}