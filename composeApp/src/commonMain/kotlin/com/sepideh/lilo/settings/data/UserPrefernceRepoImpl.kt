package com.sepideh.lilo.settings.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.settings.presentation.model.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    override val userPreferences: Flow<UserPreferences> =
        dataStore.data.map { prefs ->

            val theme = prefs[PreferencesKeys.THEME]?.let { AppTheme.valueOf(it) }
                ?: AppTheme.SYSTEM

            val language = prefs[PreferencesKeys.LANGUAGE]?.let { AppLanguage.valueOf(it) }
                ?: AppLanguage.FA

            UserPreferences(
                theme = theme,
                language = language,
                notificationsEnabled = prefs[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
            )
        }

    override suspend fun updateTheme(theme: AppTheme) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME] = theme.name
        }
    }

    override suspend fun updateLanguage(language: AppLanguage) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.LANGUAGE] = language.name
        }
    }

    override suspend fun updateNotifications(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }


}
