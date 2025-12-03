package com.sepideh.lilo.category.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getCategoryDatabaseBuilder(ctx: Context): RoomDatabase.Builder<CategoryDatabase>{
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath("category.db")
    return Room.databaseBuilder<CategoryDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}