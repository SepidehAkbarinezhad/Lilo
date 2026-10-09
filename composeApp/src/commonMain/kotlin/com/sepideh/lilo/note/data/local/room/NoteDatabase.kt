package com.sepideh.lilo.note.data.local.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [NoteEntity::class, NoteGroupEntity::class], version = 2)
@ConstructedBy(NoteDbConstructor::class)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun noteGroupDao(): NoteGroupDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object NoteDbConstructor : RoomDatabaseConstructor<NoteDatabase> {
    override fun initialize(): NoteDatabase
}
