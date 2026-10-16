package com.sepideh.lilo.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.settings.presentation.components.*
import com.sepideh.lilo.settings.presentation.model.labelRes
import com.sepideh.lilo.settings.presentation.model.iconRes
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel,
    onNavigateTo: (AppRoutes) -> Unit,
    onBack: () -> Boolean
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        onBack = onBack,
        bodyContainer = {
            SettingsScreen(
                state = state,
                onAction = { action -> viewModel.onAction(action) },
                onBack = onBack
            )
        }
    )
}

@Composable
fun SettingsScreen(state: SettingsState, onAction: (SettingsAction) -> Unit, onBack: () -> Boolean) {
    val colors = LiloExtendedTheme.colors
    val enabled = state.loaded && !state.saving
    BaseScreen(header = { BaseHeader(title = Res.string.setting_task_title, onBackPressed = onBack) }) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp).padding(top = 16.dp).navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SettingsSection(
                title = stringResource(Res.string.theme_label),
                icon = vectorResource(Res.drawable.lilo_appearance),
            ) {
                SettingsChoices(listOf(AppTheme.SYSTEM, AppTheme.LIGHT, AppTheme.DARK), state.userPreferences.theme, enabled,
                    label = { stringResource(it.labelRes) },
                    icon = { vectorResource(it.iconRes) },
                    selectionFailed = state.failedAction is SettingsAction.SelectTheme,
                    onSelect = { onAction(SettingsAction.SelectTheme(it)) })
            }
            SettingsSection(stringResource(Res.string.language_label), vectorResource(Res.drawable.lilo_language)) {
                SettingsChoices(listOf(AppLanguage.FA, AppLanguage.EN), state.userPreferences.language, enabled,
                    label = { stringResource(if (it == AppLanguage.FA) Res.string.settings_language_fa else Res.string.settings_language_en) },
                    selectionFailed = state.failedAction is SettingsAction.SelectLanguage,
                    onSelect = { onAction(SettingsAction.SelectLanguage(it)) })
            }
            ReminderSetting(state.userPreferences.notificationsEnabled, enabled) {
                onAction(SettingsAction.SetNotifications(it))
            }
            if (state.failedAction != null || state.loadFailed) {
                AppText(text = Res.string.settings_save_error, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { onAction(SettingsAction.Retry) }, colors = ButtonDefaults.textButtonColors(contentColor = colors.brand)) {
                    AppText(text = Res.string.settings_retry)
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@AppPreviews
@Composable
private fun SettingsPreview() {
    LiloPreviewWrapper { SettingsScreen(SettingsState(loaded = true), {}, { true }) }
}

@AppPreviews
@Composable
private fun MutedSettingsPreview() {
    LiloPreviewWrapper {
        SettingsScreen(SettingsState(userPreferences = com.sepideh.lilo.settings.domain.model.UserPreferences(notificationsEnabled = false), loaded = true), {}, { true })
    }
}


@androidx.compose.ui.tooling.preview.Preview(widthDp = 320, fontScale = 1.6f, locale = "fa")
@Composable
private fun LargeTextSettingsPreview() {
    LiloPreviewWrapper { SettingsScreen(SettingsState(loaded = true), {}, { true }) }
}
