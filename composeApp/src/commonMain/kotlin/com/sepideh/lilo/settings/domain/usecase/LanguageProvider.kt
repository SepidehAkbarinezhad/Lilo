package com.sepideh.lilo.settings.domain.usecase

import com.sepideh.lilo.core.domain.model.AppLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class LanguageProvider(userPreferencesManager: UserPreferencesManager) {

    private val scope = CoroutineScope(SupervisorJob())

    val languageFlow: StateFlow<AppLanguage> = userPreferencesManager.userPreferences
        .map {
            it.language }
        .stateIn(
            scope = scope,
            started = SharingStarted.Companion.Eagerly,
            initialValue = AppLanguage.FA
        )

    val currentLanguage: AppLanguage
        get() = languageFlow.value
}