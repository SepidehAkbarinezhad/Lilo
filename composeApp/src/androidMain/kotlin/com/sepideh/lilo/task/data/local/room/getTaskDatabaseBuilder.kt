package com.sepideh.lilo.task.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getTaskDatabaseBuilder(ctx: Context): RoomDatabase.Builder<TaskDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("task.db")
    return Room.databaseBuilder<TaskDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}