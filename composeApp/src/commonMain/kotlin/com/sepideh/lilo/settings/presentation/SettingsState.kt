package com.sepideh.lilo.settings.presentation

import com.sepideh.lilo.settings.domain.model.UserPreferences

data class SettingsState(
    val userPreferences: UserPreferences = UserPreferences()
)


