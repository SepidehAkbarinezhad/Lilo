package com.sepideh.lilo.task.data.local.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [TaskEntity::class, TaskGroupEntity::class],
    version = 2,
    exportSchema = true
)
@ConstructedBy(DbConstructor::class)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun taskDao(): TaskDao
    abstract fun taskGroupDao(): TaskGroupDao
}

// Room/KSP generates the actuals; the IDE inspection cannot see ungenerated targets.
@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
expect object DbConstructor: RoomDatabaseConstructor<TaskDatabase> {
    override fun initialize(): TaskDatabase
}
