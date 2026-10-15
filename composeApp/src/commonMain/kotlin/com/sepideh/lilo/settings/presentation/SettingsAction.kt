package com.sepideh.lilo.settings.presentation

import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.domain.model.AppTheme
import com.sepideh.lilo.core.presentation.BaseAction

sealed class SettingsAction : BaseAction {
    data class SetNotifications(val enabled: Boolean) : SettingsAction()
    data object Retry : SettingsAction()
    data class SelectTheme(val theme: AppTheme) : SettingsAction()
    data class SelectLanguage(val language: AppLanguage) : SettingsAction()
}