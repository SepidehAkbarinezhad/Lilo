package com.sepideh.lilo.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sepideh.lilo.task.data.TaskDatabase
import kotlinx.coroutines.Dispatchers

fun getTaskDatabaseBuilder(ctx: Context): TaskDatabase {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("task.db")
    return Room.databaseBuilder<TaskDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO).build()
}