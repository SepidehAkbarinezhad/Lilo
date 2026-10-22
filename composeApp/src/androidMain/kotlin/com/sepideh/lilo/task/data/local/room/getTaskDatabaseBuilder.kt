package com.sepideh.lilo.task.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getTaskDatabaseBuilder(ctx: Context): RoomDatabase.Builder<TaskDatabase> {
    return Room.databaseBuilder<TaskDatabase>(
        context = ctx.applicationContext,
        name = "task.db"
    ).setDriver(BundledSQLiteDriver()).addCallback(TaskGroupDefaultsCallback)
}