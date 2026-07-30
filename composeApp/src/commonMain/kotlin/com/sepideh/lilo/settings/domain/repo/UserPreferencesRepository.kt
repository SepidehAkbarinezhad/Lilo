package com.sepideh.lilo.settings.domain.repo

import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.domain.model.AppTheme
import com.sepideh.lilo.settings.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val userPreferences: Flow<UserPreferences>

    suspend fun updateTheme(theme: AppTheme)
    suspend fun updateLanguage(language: AppLanguage)
    suspend fun updateNotifications(enabled: Boolean)
}