package com.sepideh.lilo.settings.di
import com.sepideh.lilo.settings.data.UserPreferencesRepositoryImpl
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import com.sepideh.lilo.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {

    single<UserPreferencesManager> {
        UserPreferencesManager(userPreferencesRepository = get())
    }
    single<UserPreferencesRepository> {
        UserPreferencesRepositoryImpl(dataStore = get())
    }
    single<LanguageProvider> { LanguageProvider(get()) }

    single { com.sepideh.lilo.settings.domain.usecase.UpdateReminderNotifications(get(), get()) }

    viewModel {
        SettingsViewModel(userPreferencesManager = get(), updateNotifications = get())
    }
}
