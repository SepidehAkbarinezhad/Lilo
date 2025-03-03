package com.sepideh.lilo.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sepideh.lilo.task.data.TaskDatabase
import com.sepideh.lilo.task.data.instantiateImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory

fun getTaskDatabaseBuilder(): TaskDatabase{
    val dbFile = NSHomeDirectory()+"/task.db"
    return Room.databaseBuilder<TaskDatabase>(
        name = dbFile,
        factory = { TaskDatabase::class.instantiateImpl() }
    ).setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}