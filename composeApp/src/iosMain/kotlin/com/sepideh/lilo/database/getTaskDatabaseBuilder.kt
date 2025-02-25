package com.sepideh.lilo.database

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sepideh.lilo.task.data.TaskDatabase
import platform.Foundation.NSHomeDirectory

fun getTaskDatabaseBuilder(): RoomDatabase.Builder<TaskDatabase>{
    val dbFile = NSHomeDirectory()+"/task.db"
    return Room.databaseBuilder<TaskDatabase>(
        name = dbFile,
    ).setDriver(BundledSQLiteDriver())
}