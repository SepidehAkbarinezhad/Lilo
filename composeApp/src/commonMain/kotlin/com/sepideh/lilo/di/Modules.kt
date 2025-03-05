package com.sepideh.lilo.di

import com.sepideh.lilo.task.presentation.task_detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel

import org.koin.dsl.module

expect fun platformModule(): Module

val provideViewModelModule = module {
    viewModel { TaskListViewModel(get(),get()) }
    viewModel { TaskDetailViewModel(get()) }
}