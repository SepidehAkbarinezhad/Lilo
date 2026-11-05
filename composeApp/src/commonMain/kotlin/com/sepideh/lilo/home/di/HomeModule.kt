package com.sepideh.lilo.home.di

import com.sepideh.lilo.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel {
        HomeViewModel(
            taskRepository = get(),
            noteRepository = get(),
            taskMutations = get(),
            passwordRepository = get(),
            crypto = get()
        )
    }
}
