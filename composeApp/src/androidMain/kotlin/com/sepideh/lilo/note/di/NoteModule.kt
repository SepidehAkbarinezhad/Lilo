package com.sepideh.lilo.note.di

import com.sepideh.lilo.note.data.local.room.NoteDatabase
import com.sepideh.lilo.note.data.local.room.getNoteDatabaseBuilder
import org.koin.dsl.module

actual fun notePlatformModule()= module {
    single(noteDatabaseQualifier) { getNoteDatabaseBuilder(ctx = get()).build()}
    single { get<NoteDatabase>(noteDatabaseQualifier).noteDao() }

}

