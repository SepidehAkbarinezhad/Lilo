package com.sepideh.lilo.settings.domain.usecase


import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.domain.model.AppTheme
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

/*
* Domain layer manager for user preferences business logic.
* Encapsulates preference operations between data layer and presentation layer.
* Handles business rules, validation, permissions, and side effects for user preferences.
* */

class UserPreferencesManager(
    private val userPreferencesRepository: UserPreferencesRepository
) {

    val userPreferences: Flow<UserPreferences> = userPreferencesRepository.userPreferences

    /** Keep the shell usable during storage failures without overwriting saved preferences.
     * Business decisions (especially notifications) must keep using userPreferences.
     */
    val appearancePreferences: Flow<UserPreferences> = flow {
        var latest = UserPreferences()
        emitAll(userPreferences.onEach { latest = it }.retryWhen { failure, _ ->
            if (failure is CancellationException) throw failure
            emit(latest)
            delay(1_000)
            true
        })
    }

    suspend fun updateTheme(theme: AppTheme) {
        userPreferencesRepository.updateTheme(theme)
    }

    suspend fun updateLanguage(language: AppLanguage) {
        userPreferencesRepository.updateLanguage(language)
    }

}
