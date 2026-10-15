package com.sepideh.lilo.settings.presentation

import androidx.lifecycle.viewModelScope
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseViewModel
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import com.sepideh.lilo.settings.domain.usecase.UpdateReminderNotifications
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userPreferencesManager: UserPreferencesManager,
    private val updateNotifications: UpdateReminderNotifications,
) : BaseViewModel() {
    private val mutableState = MutableStateFlow(SettingsState())
    val state = mutableState.asStateFlow()
    private var observation: Job? = null

    init { observePreferences() }

    private fun observePreferences() {
        observation?.cancel()
        observation = viewModelScope.launch {
            mutableState.update { it.copy(loadFailed = false) }
            try {
                userPreferencesManager.userPreferences.collect { preferences ->
                    mutableState.update { it.copy(userPreferences = preferences, loaded = true) }
                }
            } catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutableState.update { it.copy(loadFailed = true) } }
        }
    }

    override fun onAction(action: BaseAction) {
        if (action !is SettingsAction) { super.onAction(action); return }
        if (action == SettingsAction.Retry) {
            if (state.value.loadFailed) observePreferences()
            else state.value.failedAction?.let(::savePreference)
        } else savePreference(action)
    }

    private fun savePreference(action: SettingsAction) {
        if (!state.value.loaded || state.value.saving) return
        mutableState.update { it.copy(saving = true, failedAction = null) }
        viewModelScope.launch {
            try { persist(action) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutableState.update { it.copy(failedAction = action) } }
            finally { mutableState.update { it.copy(saving = false) } }
        }
    }

    private suspend fun persist(action: SettingsAction) {
        when (action) {
            is SettingsAction.SelectTheme -> userPreferencesManager.updateTheme(action.theme)
            is SettingsAction.SelectLanguage -> userPreferencesManager.updateLanguage(action.language)
            is SettingsAction.SetNotifications -> updateNotifications(action.enabled)
            SettingsAction.Retry -> Unit
        }
    }

    override fun onResetState() = Unit
}
