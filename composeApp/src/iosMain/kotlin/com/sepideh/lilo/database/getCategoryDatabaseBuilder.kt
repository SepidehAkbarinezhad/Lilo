package com.sepideh.lilo.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.sepideh.lilo.task.data.category.CategoryDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


fun getCategoryDatabaseBuilder(): RoomDatabase.Builder<CategoryDatabase> {
    val dbFilePath = documentDirectory() + "/category.db"
    return Room.databaseBuilder<CategoryDatabase>(
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