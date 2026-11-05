package com.sepideh.lilo.settings

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.repo.UserPreferencesRepository
import com.sepideh.lilo.settings.domain.usecase.*
import com.sepideh.lilo.settings.presentation.*
import com.sepideh.lilo.task.domain.reminder.Reminder
import com.sepideh.lilo.task.domain.reminder.ReminderScheduler
import com.sepideh.lilo.task.domain.usecase.TaskMutations
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val store = ViewModelStore()
    @BeforeTest fun setup() { Dispatchers.setMain(StandardTestDispatcher()) }
    @AfterTest fun cleanup() { store.clear(); Dispatchers.resetMain() }

    @Test fun languageAndThemePersistTogetherAndFailureCanRetry() = runTest {
        val prefs = Preferences()
        val model = model(prefs)
        runCurrent()
        assertTrue(model.state.value.loaded)
        model.onAction(SettingsAction.SelectLanguage(AppLanguage.EN))
        runCurrent()
        assertEquals(AppLanguage.EN, model.state.value.userPreferences.language)
        prefs.fail = true
        model.onAction(SettingsAction.SelectTheme(AppTheme.DARK))
        runCurrent()
        assertEquals(AppTheme.SYSTEM, model.state.value.userPreferences.theme)
        assertEquals(listOf(SettingsAction.SelectTheme(AppTheme.DARK)), model.state.value.failedActions)
        assertFalse(model.state.value.saving)
        prefs.fail = false
        model.onAction(SettingsAction.Retry)
        runCurrent()
        assertEquals(AppTheme.DARK, model.state.value.userPreferences.theme)
        assertEquals(AppLanguage.EN, model.state.value.userPreferences.language)
        assertTrue(model.state.value.failedActions.isEmpty())
    }
    @Test fun failedNotificationWriteDoesNotMisrepresentSavedSwitch() = runTest {
        val prefs = Preferences()
        val model = model(prefs)
        runCurrent()
        prefs.fail = true
        model.onAction(SettingsAction.SetNotifications(false))
        runCurrent()
        assertTrue(model.state.value.userPreferences.notificationsEnabled)
        assertEquals(listOf(SettingsAction.SetNotifications(false)), model.state.value.failedActions)
        prefs.fail = false
        model.onAction(SettingsAction.Retry)
        runCurrent()
        assertFalse(model.state.value.userPreferences.notificationsEnabled)
    }
    @Test fun rapidChoicesDuringNotificationRefreshAreSavedInTapOrder() = runTest {
        val prefs = Preferences()
        val choices = mutableListOf<SettingsAction>()
        val recordingPrefs = object : UserPreferencesRepository by prefs {
            override suspend fun updateTheme(theme: AppTheme) {
                choices += SettingsAction.SelectTheme(theme)
                prefs.updateTheme(theme)
            }
            override suspend fun updateLanguage(language: AppLanguage) {
                choices += SettingsAction.SelectLanguage(language)
                prefs.updateLanguage(language)
            }
        }
        val release = CompletableDeferred<Unit>()
        val scheduler = object : ReminderScheduler by Scheduler() {
            override suspend fun refreshNotificationPreference(reminders: suspend () -> List<Reminder>) = release.await()
        }
        val model = model(recordingPrefs, scheduler)
        runCurrent()
        model.onAction(SettingsAction.SetNotifications(false))
        runCurrent()
        val taps = listOf(SettingsAction.SelectTheme(AppTheme.DARK), SettingsAction.SelectLanguage(AppLanguage.EN),
            SettingsAction.SelectTheme(AppTheme.SYSTEM))
        try {
            assertTrue(model.state.value.saving)
            taps.forEach(model::onAction)
            runCurrent()
            assertEquals(UserPreferences(notificationsEnabled = false), model.state.value.userPreferences)
        } finally { release.complete(Unit) }
        runCurrent()
        assertEquals(taps, choices)
        assertEquals(UserPreferences(AppTheme.SYSTEM, AppLanguage.EN, false), model.state.value.userPreferences)
        assertFalse(model.state.value.saving)
    }

    @Test fun retryAfterRapidFailuresRespectsANewerChoice() = runTest {
        val prefs = Preferences()
        val model = model(prefs)
        runCurrent()
        prefs.fail = true
        model.onAction(SettingsAction.SelectTheme(AppTheme.DARK))
        model.onAction(SettingsAction.SelectLanguage(AppLanguage.EN))
        runCurrent()
        assertEquals(UserPreferences(), model.state.value.userPreferences)
        assertEquals(listOf(SettingsAction.SelectTheme(AppTheme.DARK), SettingsAction.SelectLanguage(AppLanguage.EN)),
            model.state.value.failedActions)

        prefs.fail = false
        model.onAction(SettingsAction.SelectTheme(AppTheme.LIGHT))
        model.onAction(SettingsAction.Retry)
        runCurrent()
        assertEquals(UserPreferences(AppTheme.LIGHT, AppLanguage.EN), model.state.value.userPreferences)
        assertTrue(model.state.value.failedActions.isEmpty())
    }

    private fun model(prefs: UserPreferencesRepository, scheduler: ReminderScheduler = Scheduler()) =
        SettingsViewModel(UserPreferencesManager(prefs), UpdateReminderNotifications(prefs, TaskMutations(Tasks(), scheduler)))
            .also { store.put("settings", it) }
}
