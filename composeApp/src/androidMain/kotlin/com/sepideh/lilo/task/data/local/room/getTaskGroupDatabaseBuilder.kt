package com.sepideh.lilo.task.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.room.RoomDatabase

fun getTaskGroupDatabaseBuilder(ctx: Context): RoomDatabase.Builder<TaskGroupDatabase>{
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("task_groups.db")
    return Room.databaseBuilder<TaskGroupDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver()).addCallback(TaskGroupDefaultsCallback)
}