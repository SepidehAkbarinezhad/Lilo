package com.sepideh.lilo.core.data.local.dataStore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    // App settings
    val THEME = stringPreferencesKey("theme")
    val LANGUAGE = stringPreferencesKey("language")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
}