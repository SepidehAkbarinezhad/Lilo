package com.sepideh.lilo.note.data.local.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sepideh.lilo.note.data.local.room.NoteDatabase
import com.sepideh.lilo.task.data.local.room.TaskDatabase

fun getNoteDatabaseBuilder(ctx: Context): RoomDatabase.Builder<NoteDatabase> {
    return Room.databaseBuilder<NoteDatabase>(
        context = ctx.applicationContext,
        name = "note.db"
    )
}