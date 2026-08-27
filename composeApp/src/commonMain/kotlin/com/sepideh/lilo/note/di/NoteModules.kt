package com.sepideh.lilo.note.di

import com.sepideh.lilo.note.data.local.room.NoteDatabase
import com.sepideh.lilo.note.data.repoImpl.NoteRepoImpl
import com.sepideh.lilo.note.domain.repository.NoteRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val noteDatabaseQualifier = named("noteDatabase")

expect fun notePlatformModule(): Module

val noteModule = module {

    single { get<NoteDatabase>(noteDatabaseQualifier).noteDao() }

    single<NoteRepository> {
        NoteRepoImpl(
            noteDao = get()
        )
    }
}