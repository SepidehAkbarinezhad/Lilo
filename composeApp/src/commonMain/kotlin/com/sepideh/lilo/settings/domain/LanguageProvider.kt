package com.sepideh.lilo.settings.domain

import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import com.sepideh.lilo.settings.presentation.model.AppLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.SupervisorJob

//Domain-layer helper → accessible everywhere
class LanguageProvider(userPreferencesManager: UserPreferencesManager) {

    private val scope = CoroutineScope(SupervisorJob())

    val languageFlow: StateFlow<AppLanguage> = userPreferencesManager.userPreferences
        .map { it.language }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = AppLanguage.FA
        )

    val currentLanguage: AppLanguage
        get() = languageFlow.value
}
