package com.sepideh.lilo.category.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT COUNT(*) FROM task_groups")
    suspend fun count(): Int

    @Transaction
    suspend fun seedIfEmpty(defaults: List<CategoryEntity>) {
        if (count() == 0) defaults.forEach { upsert(it) }
    }

    @Upsert
    suspend fun upsert(category: CategoryEntity): Long

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("DELETE FROM task_groups WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM task_groups WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: Long): CategoryEntity?

    @Query("SELECT * FROM task_groups")
    fun getAllCategories(): Flow<List<CategoryEntity>>
}