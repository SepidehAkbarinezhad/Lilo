package com.sepideh.lilo.settings.presentation

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.settings.domain.UserPreferencesRepository
import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.settings.presentation.model.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(val userPreferencesRepository: UserPreferencesRepository): BaseViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state

    init {
        viewModelScope.launch {
            userPreferencesRepository.userPreferences.collect { prefs ->
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
            is SettingsAction.ToggleNotifications -> updateNotifications(enabled = action.enabled)
        }
    }
    fun updateTheme(theme: AppTheme) {
        viewModelScope.launch {
            userPreferencesRepository.updateTheme(theme)
        }
    }
    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            userPreferencesRepository.updateLanguage(language)
        }
    }
    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.updateNotifications(enabled)
        }
    }


    override fun onResetState() {

    }
}