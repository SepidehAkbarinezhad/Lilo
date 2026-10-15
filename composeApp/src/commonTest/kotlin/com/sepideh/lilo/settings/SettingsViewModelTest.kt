package com.sepideh.lilo.settings

import androidx.lifecycle.ViewModelStore
import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.settings.domain.usecase.*
import com.sepideh.lilo.settings.presentation.*
import com.sepideh.lilo.task.domain.usecase.TaskMutations
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
        assertNotNull(model.state.value.failedAction)
        assertFalse(model.state.value.saving)
        prefs.fail = false
        model.onAction(SettingsAction.Retry)
        runCurrent()
        assertEquals(AppTheme.DARK, model.state.value.userPreferences.theme)
        assertEquals(AppLanguage.EN, model.state.value.userPreferences.language)
        assertNull(model.state.value.failedAction)
    }
    @Test fun failedNotificationWriteDoesNotMisrepresentSavedSwitch() = runTest {
        val prefs = Preferences()
        val model = model(prefs)
        runCurrent()
        prefs.fail = true
        model.onAction(SettingsAction.SetNotifications(false))
        runCurrent()
        assertTrue(model.state.value.userPreferences.notificationsEnabled)
        assertNotNull(model.state.value.failedAction)
        prefs.fail = false
        model.onAction(SettingsAction.Retry)
        runCurrent()
        assertFalse(model.state.value.userPreferences.notificationsEnabled)
    }
    private fun model(prefs: Preferences) = SettingsViewModel(UserPreferencesManager(prefs),
        UpdateReminderNotifications(prefs, TaskMutations(Tasks(), Scheduler()))).also { store.put("settings", it) }
}
