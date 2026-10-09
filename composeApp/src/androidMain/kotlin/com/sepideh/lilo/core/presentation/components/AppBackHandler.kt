package com.sepideh.lilo.core.presentation.components
import androidx.compose.runtime.Composable
@Composable actual fun AppBackHandler(enabled: Boolean, onBack: () -> Unit) { androidx.activity.compose.BackHandler(enabled, onBack) }
