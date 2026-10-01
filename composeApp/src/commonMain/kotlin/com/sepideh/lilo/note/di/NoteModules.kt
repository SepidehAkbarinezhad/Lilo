package com.sepideh.lilo.note.di

import com.sepideh.lilo.note.data.local.room.NoteDatabase
import com.sepideh.lilo.note.data.repoImpl.NoteRepoImpl
import com.sepideh.lilo.note.domain.repository.NoteRepository
import com.sepideh.lilo.note.presentation.detail.NoteDetailViewModel
import com.sepideh.lilo.note.presentation.list.NoteListViewModel
import com.sepideh.lilo.task.presentation.detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.list.TaskListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
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

    viewModel {
        NoteListViewModel(
            languageProvider = get(),
            noteRepository = get(),
            categoryRepository = get(),
        )
    }
    viewModel {
        NoteDetailViewModel(
            categoryFactory = get(),
            languageProvider = get(),
            taskRepository = get(),
            categoryRepository = get(),
            reminderScheduler = get(),
            permissionManager = get()
        )
    }
}