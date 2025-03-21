package com.sepideh.lilo.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.sepideh.lilo.task.data.TaskDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


fun getTaskDatabaseBuilder(): RoomDatabase.Builder<TaskDatabase> {
    val dbFilePath = documentDirectory() + "/task.db"
    return Room.databaseBuilder<TaskDatabase>(
        name = dbFilePath,
    )
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