package com.sepideh.lilo.settings.domain.usecase

import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.settings.presentation.model.AppTheme
import kotlinx.coroutines.flow.Flow

/*
* Domain layer manager for user preferences business logic.
* Encapsulates preference operations between data layer and presentation layer.
* Handles business rules, validation, permissions, and side effects for user preferences.
* */

class UserPreferencesManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {

    val userPreferences: Flow<UserPreferences> = userPreferencesRepository.userPreferences

    suspend fun updateTheme(theme: AppTheme) {
        userPreferencesRepository.updateTheme(theme)
    }

    suspend fun updateLanguage(language: AppLanguage) {
        userPreferencesRepository.updateLanguage(language)
    }

    suspend fun resetToDefaults() {
        userPreferencesRepository.updateTheme(AppTheme.SYSTEM)
        userPreferencesRepository.updateLanguage(AppLanguage.FA)
        userPreferencesRepository.updateNotifications(true)
    }

}