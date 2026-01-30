package com.sepideh.lilo.settings.domain.model

import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.settings.presentation.model.AppTheme

data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.FA,
    val notificationsEnabled: Boolean = true
)