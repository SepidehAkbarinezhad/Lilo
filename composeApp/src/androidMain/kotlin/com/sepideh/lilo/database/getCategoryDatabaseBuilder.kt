package com.sepideh.lilo.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sepideh.lilo.task.data.TaskDatabase
import com.sepideh.lilo.task.data.category.CategoryDatabase

fun getCategoryDatabaseBuilder(ctx: Context): RoomDatabase.Builder<CategoryDatabase>{
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("category.db")
    return Room.databaseBuilder<CategoryDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}