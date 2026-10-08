package com.sepideh.lilo.task.data.local.room

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


fun getTaskGroupDatabaseBuilder(): RoomDatabase.Builder<TaskGroupDatabase> {
    val dbFilePath = documentDirectory() + "/task_groups.db"
    return Room.databaseBuilder<TaskGroupDatabase>(
        name = dbFilePath,
    ).setDriver(BundledSQLiteDriver()).addCallback(TaskGroupDefaultsCallback)
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}