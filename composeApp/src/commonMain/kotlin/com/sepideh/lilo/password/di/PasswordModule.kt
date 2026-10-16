package com.sepideh.lilo.password.di

import com.sepideh.lilo.password.data.PasswordRepositoryImpl
import com.sepideh.lilo.password.data.local.PasswordDatabase
import com.sepideh.lilo.password.domain.PasswordRepository
import com.sepideh.lilo.password.presentation.list.PasswordListViewModel
import com.sepideh.lilo.password.presentation.detail.PasswordDetailViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

expect fun passwordPlatformModule(): Module
val passwordModule = module {
    single { get<PasswordDatabase>().passwords() }
    single { get<PasswordDatabase>().services() }
    single<com.sepideh.lilo.password.domain.PasswordServiceRepository> { com.sepideh.lilo.password.data.PasswordServiceRepositoryImpl(get()) }
    single<PasswordRepository> { PasswordRepositoryImpl(get(), get(), get()) }
    viewModel { com.sepideh.lilo.password.presentation.components.PasswordServicesViewModel(get()) }
    viewModel { PasswordListViewModel(get(), get(), get()) }
    viewModel { PasswordDetailViewModel(get(), get()) }
}
