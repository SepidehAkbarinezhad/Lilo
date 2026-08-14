package com.sepideh.lilo.task.di

import com.sepideh.lilo.category.data.local.room.CategoryDatabase
import com.sepideh.lilo.category.data.reposirotyImpl.CategoryRepositoryImpl
import com.sepideh.lilo.category.di.categoryDatabaseQualifier
import com.sepideh.lilo.category.domain.repository.CategoryRepository
import com.sepideh.lilo.task.data.local.room.TaskDatabase
import com.sepideh.lilo.task.data.repositoryImpl.TaskRepoImpl
import com.sepideh.lilo.task.domain.repository.TaskRepository
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val taskDatabaseQualifier = named("taskDatabase")

expect fun taskPlatformModule(): Module

val taskModule = module {

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
            reminderScheduler = get()
        )
    }
    viewModel {
        TaskDetailViewModel(
            categoryFactory = get(),
            languageProvider = get(),
            taskRepository = get(),
            categoryRepository = get(),
            reminderScheduler = get(),
            permissionManager = get()
        )
    }
}
