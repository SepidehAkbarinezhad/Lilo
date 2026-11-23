package com.sepideh.lilo.settings.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.domain.model.*
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.utils.LiloInfo
import com.sepideh.lilo.settings.presentation.components.*
import com.sepideh.lilo.settings.presentation.model.labelRes
import com.sepideh.lilo.settings.presentation.model.iconRes
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSpacing
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject

@Composable
fun SettingsScreenRoot(
    viewModel: SettingsViewModel,
    onNavigateTo: (AppRoutes) -> Unit,
    onBack: () -> Boolean
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val liloInfo = koinInject<LiloInfo>()
    val appVersion = remember(liloInfo) { liloInfo.appVersion }
    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        onBack = onBack,
        bodyContainer = {
            SettingsScreen(
                state = state,
                onAction = { action -> viewModel.onAction(action) },
                onBack = onBack,
                appVersion = appVersion,
            )
        }
    )
}

@Composable
fun SettingsScreen(state: SettingsState, onAction: (SettingsAction) -> Unit, onBack: () -> Boolean, appVersion: String = "") {
    val colors = LiloExtendedTheme.colors
    BaseScreen(header = { BaseHeader(title = Res.string.setting_task_title, onBackPressed = onBack) }) {
        Column(Modifier.fillMaxSize().navigationBarsPadding()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SettingsSection(
                    title = stringResource(Res.string.theme_label),
                    icon = vectorResource(Res.drawable.lilo_appearance),
                ) {
                    SettingsChoices(listOf(AppTheme.SYSTEM, AppTheme.LIGHT, AppTheme.DARK), state.userPreferences.theme, state.loaded,
                        label = { stringResource(it.labelRes) },
                        icon = { vectorResource(it.iconRes) },
                        onSelect = { onAction(SettingsAction.SelectTheme(it)) })
                }
                SettingsSection(stringResource(Res.string.language_label), vectorResource(Res.drawable.lilo_language)) {
                    SettingsChoices(listOf(AppLanguage.FA, AppLanguage.EN), state.userPreferences.language, state.loaded,
                        label = { stringResource(if (it == AppLanguage.FA) Res.string.settings_language_fa else Res.string.settings_language_en) },
                        onSelect = { onAction(SettingsAction.SelectLanguage(it)) })
                }
                ReminderSetting(state.userPreferences.notificationsEnabled, state.loaded && !state.saving) {
                    onAction(SettingsAction.SetNotifications(it))
                }
                if (state.failedActions.isNotEmpty() || state.loadFailed) {
                    AppText(text = Res.string.settings_save_error, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { onAction(SettingsAction.Retry) }, colors = ButtonDefaults.textButtonColors(contentColor = colors.brand)) {
                        AppText(text = Res.string.settings_retry)
                    }
                }
            }
            if (appVersion.isNotBlank()) {
                AppText(
                    // Keep dotted version identifiers in LTR order inside Persian text.
                    text = stringResource(Res.string.settings_app_version, "\u2066$appVersion\u2069"),
                    textType = TextType.Caption,
                    color = colors.textSupporting,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = LiloSpacing.Item),
                )
            }
        }
    }
}

@AppPreviews
@Composable
private fun SettingsPreview() {
    LiloPreviewWrapper { SettingsScreen(SettingsState(loaded = true), {}, { true }, appVersion = "1.0.0") }
}

@AppPreviews
@Composable
private fun MutedSettingsPreview() {
    LiloPreviewWrapper {
        SettingsScreen(SettingsState(userPreferences = com.sepideh.lilo.settings.domain.model.UserPreferences(notificationsEnabled = false), loaded = true), {}, { true }, appVersion = "1.0.0")
    }
}


@androidx.compose.ui.tooling.preview.Preview(widthDp = 320, fontScale = 1.6f, locale = "fa")
@Composable
private fun LargeTextSettingsPreview() {
    LiloPreviewWrapper { SettingsScreen(SettingsState(loaded = true), {}, { true }, appVersion = "1.0.0") }
}
