package com.sepideh.lilo.task.data.local.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [TaskGroupEntity::class],
    version = 1
)
@ConstructedBy(TaskGroupDbConstructor::class)
abstract class TaskGroupDatabase : RoomDatabase(){
    abstract fun taskGroupDao(): TaskGroupDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object TaskGroupDbConstructor: RoomDatabaseConstructor<TaskGroupDatabase> {
    override fun initialize(): TaskGroupDatabase
}
