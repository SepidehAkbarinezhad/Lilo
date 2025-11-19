package com.sepideh.lilo.settings.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseHeader
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.BaseScreen
import com.sepideh.lilo.core.presentation.components.AppText
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
    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        onBack = onBack,
        bodyContainer = {
            SettingsScreen()
        }
    )

}

@Composable
fun SettingsScreen() {
    BaseScreen(header = {
        BaseHeader(title = Res.string.setting_task_title)
    }, content = {
        SettingsItemContainer(
            icon = Res.drawable.ic_theme,
            title = Res.string.theme_label
        ) {
            AppTheme.entries.forEach { theme ->
                SettingItem(label = theme.label, selected = "", onSelected = {})
            }
        }
        SettingsItemContainer(
            icon = Res.drawable.ic_language,
            title = Res.string.language_label
        ) {
            AppLanguage.entries.forEach { language ->
                SettingItem(label = language.label, selected = "", onSelected = {})
            }
        }

    })
}




