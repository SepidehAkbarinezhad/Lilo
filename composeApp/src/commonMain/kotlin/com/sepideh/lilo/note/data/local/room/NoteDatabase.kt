package com.sepideh.lilo.note.data.local.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.sepideh.lilo.task.data.local.room.TaskDao
import com.sepideh.lilo.task.data.local.room.TaskEntity

@Database(
    entities = [NoteEntity::class],
    version = 1
)
@ConstructedBy(DbConstructor::class)
abstract class NoteDatabase : RoomDatabase(){
    abstract fun noteDao(): NoteDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object DbConstructor: RoomDatabaseConstructor<NoteDatabase> {
    override fun initialize(): NoteDatabase
}
