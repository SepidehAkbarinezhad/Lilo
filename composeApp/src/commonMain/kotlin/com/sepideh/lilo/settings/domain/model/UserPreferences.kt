package com.sepideh.lilo.settings.domain.model

import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.domain.model.AppTheme


data class UserPreferences(
    val theme: AppTheme = AppTheme.SYSTEM,
    val language: AppLanguage = AppLanguage.FA,
    val notificationsEnabled: Boolean = true
)

