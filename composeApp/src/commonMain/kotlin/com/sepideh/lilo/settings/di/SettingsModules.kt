package com.sepideh.lilo.settings.di

import com.sepideh.lilo.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val settingsModule = module {
    viewModel { SettingsViewModel() }
}
