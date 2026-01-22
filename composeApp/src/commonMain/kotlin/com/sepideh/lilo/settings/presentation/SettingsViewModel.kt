package com.sepideh.lilo.settings.presentation

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.core.utils.changeLanguage
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.settings.presentation.model.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(val userPreferencesManager: UserPreferencesManager): BaseViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init {
        viewModelScope.launch {
            userPreferencesManager.userPreferences.collect { prefs ->
                _state.update { currentState ->
                    currentState.copy(userPreferences = prefs)
                }
            }
        }
    }

    override fun onAction(action: BaseAction) {
        super.onAction(action)
        when(action){
            is SettingsAction.SelectLanguage -> updateLanguage(language = action.language)
            is SettingsAction.SelectTheme -> updateTheme(theme = action.theme)
        }
    }
    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            userPreferencesManager.updateTheme(theme)
        }
    }
    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            userPreferencesManager.updateLanguage(language)
            changeLanguage(language.code)
        }
    }


    override fun onResetState() {

    }
}