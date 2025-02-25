package com.sepideh.lilo.di

import com.sepideh.lilo.task.presentation.task_detail.TaskDetailViewModel
import com.sepideh.lilo.task.presentation.task_list.TaskListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf

import org.koin.dsl.module

val sharedModule = module {
    viewModel{TaskListViewModel(get())}
    viewModel{TaskDetailViewModel(get())}
}