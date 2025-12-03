package com.sepideh.lilo.task.di

import com.sepideh.lilo.category.di.categoryDatabaseQualifier
import com.sepideh.lilo.settings.presentation.SettingsViewModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val taskDatabaseQualifier = named("taskDatabase")

expect fun taskPlatformModule(): Module

val viewModelModule = module {
    viewModel { TaskListViewModel(languageProvider = get(),taskDatabase = get(taskDatabaseQualifier), categoryDatabase = get(categoryDatabaseQualifier) ,reminderScheduler = get()) }
    viewModel { TaskDetailViewModel(categoryFactory = get(),taskDatabase = get(taskDatabaseQualifier), categoryDatabase = get(categoryDatabaseQualifier), reminderScheduler = get(), permissionManager = get()) }
}
