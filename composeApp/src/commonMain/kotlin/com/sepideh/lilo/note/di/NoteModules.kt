package com.sepideh.lilo.note.di
import com.sepideh.lilo.note.data.local.room.NoteDatabase
import com.sepideh.lilo.note.data.repository.*
import com.sepideh.lilo.note.domain.repository.*
import com.sepideh.lilo.note.domain.usecase.NoteMutations
import com.sepideh.lilo.note.presentation.detail.NoteDetailViewModel
import com.sepideh.lilo.note.presentation.list.NoteListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
val noteDatabaseQualifier = named("noteDatabase")
expect fun notePlatformModule(): Module
val noteModule = module {
 single { get<NoteDatabase>(noteDatabaseQualifier).noteDao() }
 single { get<NoteDatabase>(noteDatabaseQualifier).noteGroupDao() }
 single<NoteRepository> { NoteRepositoryImpl(get()) }
 single<NoteGroupRepository> { NoteGroupRepositoryImpl(get()) }
 single { NoteMutations(get(), get()) }
 viewModel { NoteListViewModel(get(), get(), get(), get()) }
 viewModel { NoteDetailViewModel(get(), get(), get(), get()) }
}
