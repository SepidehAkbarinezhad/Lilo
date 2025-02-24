package com.sepideh.lilo.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TaskEntity::class],
    version = 1
)
abstract class TaskDatabase : RoomDatabase(){
    abstract fun taskDao():TaskDao
}