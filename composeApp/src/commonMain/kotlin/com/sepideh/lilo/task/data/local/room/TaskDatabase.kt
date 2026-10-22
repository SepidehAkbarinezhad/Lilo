package com.sepideh.lilo.task.data.local.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [TaskEntity::class, TaskGroupEntity::class],
    version = 1,
    exportSchema = true
)
@ConstructedBy(DbConstructor::class)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun taskDao(): TaskDao
    abstract fun taskGroupDao(): TaskGroupDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DbConstructor: RoomDatabaseConstructor<TaskDatabase> {
    override fun initialize(): TaskDatabase
}
