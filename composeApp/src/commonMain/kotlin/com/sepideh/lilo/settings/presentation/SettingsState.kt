package com.sepideh.lilo.settings.presentation

import com.sepideh.lilo.settings.domain.UserPreferences

data class SettingsState(
    val userPreferences: UserPreferences = UserPreferences()
)


