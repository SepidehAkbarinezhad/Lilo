package com.sepideh.lilo.core.presentation.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sepideh.lilo.settings.presentation.SettingsScreen
import com.sepideh.lilo.settings.presentation.SettingsState

@Preview
@Composable
fun SettingsScreenPreview(modifier: Modifier = Modifier) {
    SettingsScreen(state = SettingsState(), onAction = {}, onBack = { true })
}