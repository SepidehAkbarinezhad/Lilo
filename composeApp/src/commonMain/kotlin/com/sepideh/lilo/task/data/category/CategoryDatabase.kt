package com.sepideh.lilo.task.data.category

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [CategoryEntity::class],
    version = 1
)
@ConstructedBy(DbConstructor::class)
abstract class CategoryDatabase : RoomDatabase(){
    abstract fun categoryDao(): CategoryDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DbConstructor: RoomDatabaseConstructor<CategoryDatabase> {
    override fun initialize(): CategoryDatabase
}
