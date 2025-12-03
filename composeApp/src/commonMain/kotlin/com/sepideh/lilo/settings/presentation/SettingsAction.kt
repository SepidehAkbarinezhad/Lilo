package com.sepideh.lilo.settings.presentation

import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.settings.presentation.model.AppTheme

sealed class SettingsAction : BaseAction {
    data class SelectTheme(val theme: AppTheme) : SettingsAction()
    data class SelectLanguage(val language: AppLanguage) : SettingsAction()
}