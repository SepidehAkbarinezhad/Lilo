package com.sepideh.lilo.settings

import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class AppearanceRecoveryTest {
    @Test fun firstReadFailureAllowsStartupThenRecoversWithoutWritingDefaults() = runTest {
        var attempts = 0
        val saved = UserPreferences(AppTheme.DARK, AppLanguage.EN, false)
        val manager = manager(flow { if (attempts++ == 0) error("Storage unavailable"); emit(saved) })
        assertEquals(listOf(UserPreferences(), saved), manager.appearancePreferences.take(2).toList())
    }

    @Test fun laterFailureKeepsLastAppearanceAndRawReadsStillFail() = runTest {
        val saved = UserPreferences(AppTheme.DARK, AppLanguage.EN, false)
        val manager = manager(flow { emit(saved); error("Read failed") })
        assertEquals(listOf(saved, saved), manager.appearancePreferences.take(2).toList())
        assertFailsWith<IllegalStateException> { manager.userPreferences.toList() }
    }

    @Test fun cancellationIsNeverConvertedIntoDefaultPreferences() = runTest {
        val manager = manager(flow { throw CancellationException("Stopped") })
        assertFailsWith<CancellationException> { manager.appearancePreferences.first() }
    }

    private fun manager(reads: Flow<UserPreferences>) = UserPreferencesManager(object : UserPreferencesRepository {
        override val userPreferences = reads
        override suspend fun updateTheme(theme: AppTheme): Unit = error("Must not write fallback")
        override suspend fun updateLanguage(language: AppLanguage): Unit = error("Must not write fallback")
        override suspend fun updateNotifications(enabled: Boolean): Unit = error("Must not write fallback")
    })
}
