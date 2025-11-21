package com.sepideh.lilo.settings.di

import com.sepideh.lilo.settings.data.UserPreferencesRepositoryImpl
import com.sepideh.lilo.settings.domain.UserPreferencesRepository
import com.sepideh.lilo.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val settingsModule = module {

    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(dataStore = get())
    }

    viewModel {
        SettingsViewModel(userPreferencesRepository = get())
    }
}
