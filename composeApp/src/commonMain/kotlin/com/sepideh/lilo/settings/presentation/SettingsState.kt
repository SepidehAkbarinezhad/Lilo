package com.sepideh.lilo.settings.presentation

import com.sepideh.lilo.settings.domain.model.UserPreferences

data class SettingsState(
    val userPreferences: UserPreferences = UserPreferences(),
    val loaded: Boolean = false,
    val saving: Boolean = false,
    val failedAction: SettingsAction? = null,
    val loadFailed: Boolean = false,
)
