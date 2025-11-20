package com.sepideh.lilo.core.data.local.dataStore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    // App settings
    val THEME = stringPreferencesKey("theme")
    val LANGUAGE = stringPreferencesKey("language")
}