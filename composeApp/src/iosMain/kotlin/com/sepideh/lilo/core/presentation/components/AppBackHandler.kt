package com.sepideh.lilo.core.presentation.components
import androidx.compose.runtime.Composable
@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) { androidx.compose.ui.backhandler.BackHandler(enabled, onBack) }
