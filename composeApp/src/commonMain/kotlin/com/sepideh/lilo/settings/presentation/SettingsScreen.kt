package com.sepideh.lilo.settings.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseHeader
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.BaseScreen
import com.sepideh.lilo.settings.presentation.components.SettingItem
import com.sepideh.lilo.settings.presentation.components.SettingsItemContainer
import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.settings.presentation.model.AppTheme
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.ic_language
import lilo.composeapp.generated.resources.ic_theme
import lilo.composeapp.generated.resources.language_label
import lilo.composeapp.generated.resources.setting_task_title
import lilo.composeapp.generated.resources.theme_label

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
                onAction = { action -> viewModel.onAction(action) })
        }
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit
) {
    with(state.userPreferences) {
        BaseScreen(header = {
            BaseHeader(title = Res.string.setting_task_title)
        }, content = {
            SettingsItemContainer(
                icon = Res.drawable.ic_theme,
                title = Res.string.theme_label
            ) {
                AppTheme.entries.forEach { themeV ->
                    SettingItem(
                        label = themeV.label, value = themeV , selectedValue = theme,
                        onSelected = { onAction(SettingsAction.SelectTheme(theme = themeV)) })
                }
            }
            SettingsItemContainer(
                icon = Res.drawable.ic_language,
                title = Res.string.language_label
            ) {
                AppLanguage.entries.forEach { languageV ->
                    SettingItem(
                        label = languageV.label,
                        value = languageV,
                        selectedValue = language,
                        onSelected = { onAction(SettingsAction.SelectLanguage(language = languageV)) })
                }
            }

        })
    }

}




