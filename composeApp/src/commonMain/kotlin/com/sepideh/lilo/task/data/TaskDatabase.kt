package com.sepideh.lilo.task.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [TaskEntity::class],
    version = 1
)
@ConstructedBy(DbConstructor::class)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun taskDao(): TaskDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DbConstructor: RoomDatabaseConstructor<TaskDatabase> {
    override fun initialize(): TaskDatabase
}
