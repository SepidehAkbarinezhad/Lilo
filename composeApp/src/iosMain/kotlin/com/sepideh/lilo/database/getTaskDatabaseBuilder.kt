package com.sepideh.lilo.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sepideh.lilo.task.data.TaskDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

fun getTaskDatabaseBuilder(): TaskDatabase{
    val dbFile = NSHomeDirectory()+"/task.db"
    return Room.databaseBuilder<TaskDatabase>(
        name = dbFile,
    ).setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}