package com.sepideh.lilo.task.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getTaskDatabaseBuilder(ctx: Context): RoomDatabase.Builder<TaskDatabase> {
    return Room.databaseBuilder<TaskDatabase>(
        context = ctx.applicationContext,
        name = "task.db"
    )
}