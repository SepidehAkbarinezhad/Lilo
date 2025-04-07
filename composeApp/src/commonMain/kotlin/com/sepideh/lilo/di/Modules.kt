package com.sepideh.lilo.di

import com.sepideh.lilo.task.presentation.task_detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val taskDatabaseQualifier = named("taskDatabase")
val categoryDatabaseQualifier = named("categoryDatabase")

expect fun platformModule(): Module

val provideViewModelModule = module {
    viewModel { TaskListViewModel(taskDatabase = get(taskDatabaseQualifier), categoryDatabase = get(categoryDatabaseQualifier)) }
    viewModel { TaskDetailViewModel(taskDatabase = get(taskDatabaseQualifier), categoryDatabase = get(categoryDatabaseQualifier), reminderScheduler = get()) }
}
