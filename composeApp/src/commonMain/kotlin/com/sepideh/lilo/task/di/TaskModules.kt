package com.sepideh.lilo.task.di

import com.sepideh.lilo.task.data.local.room.TaskDatabase
import com.sepideh.lilo.task.data.repository.TaskRepoImpl
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.presentation.detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.list.TaskListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val taskDatabaseQualifier = named("taskDatabase")

expect fun taskPlatformModule(): Module

val taskModule = module {
    single<com.sepideh.lilo.task.domain.reminder.ReminderPermissions> { com.sepideh.lilo.task.data.PlatformReminderPermissions(get()) }
    single { com.sepideh.lilo.task.domain.usecase.TaskMutations(get(), get()) }

    single { get<TaskDatabase>(taskDatabaseQualifier).taskDao() }

    single<TaskRepository> {
        TaskRepoImpl(
            taskDao = get()
        )
    }

    viewModel {
        TaskListViewModel(
            languageProvider = get(),
            taskRepository = get(),
            categoryRepository = get(),
            mutations = get()
        )
    }
    viewModel {
        TaskDetailViewModel(
            categoryFactory = get(),
            languageProvider = get(),
            taskRepository = get(),
            categoryRepository = get(),
            mutations = get(),
            permissions = get()
        )
    }
}
