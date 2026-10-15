package com.sepideh.lilo.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.settings.data.*
import com.sepideh.lilo.settings.domain.model.UserPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class UserPreferencesRepositoryTest {
    @Test fun unknownSavedEnumsFallBackWithoutLosingMute() = runTest {
        val store = Store(preferencesOf(PreferencesKeys.THEME to "OLD_THEME", PreferencesKeys.LANGUAGE to "unknown", PreferencesKeys.NOTIFICATIONS_ENABLED to false))
        val prefs = UserPreferencesRepositoryImpl(store).userPreferences.first()
        assertEquals(UserPreferences(notificationsEnabled = false), prefs)
    }
    @Test fun updatesKeepOtherPreferencesAndSurviveNewRepository() = runTest {
        val store = Store(emptyPreferences())
        val repo = UserPreferencesRepositoryImpl(store)
        repo.updateNotifications(false)
        repo.updateTheme(AppTheme.DARK)
        repo.updateLanguage(AppLanguage.EN)
        assertEquals(UserPreferences(AppTheme.DARK, AppLanguage.EN, false), UserPreferencesRepositoryImpl(store).userPreferences.first())
    }
    private class Store(initial: androidx.datastore.preferences.core.Preferences) : DataStore<androidx.datastore.preferences.core.Preferences> {
        override val data = MutableStateFlow(initial)
        override suspend fun updateData(transform: suspend (androidx.datastore.preferences.core.Preferences) -> androidx.datastore.preferences.core.Preferences): androidx.datastore.preferences.core.Preferences {
            return transform(data.value).also { data.value = it }
        }
    }
}
