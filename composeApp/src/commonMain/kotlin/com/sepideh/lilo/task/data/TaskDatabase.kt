package com.sepideh.lilo.task.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TaskEntity::class],
    version = 1
)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun taskDao(): TaskDao
}