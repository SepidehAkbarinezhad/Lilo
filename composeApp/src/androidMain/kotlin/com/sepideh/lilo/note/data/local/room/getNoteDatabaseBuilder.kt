package com.sepideh.lilo.note.data.local.room

import android.content.Context
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sepideh.lilo.note.data.local.room.NoteDatabase

fun getNoteDatabaseBuilder(ctx: Context): RoomDatabase.Builder<NoteDatabase> {
    return Room.databaseBuilder<NoteDatabase>(
        context = ctx.applicationContext,
        name = "note.db"
    ).setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addCallback(NoteDefaultsCallback)
}