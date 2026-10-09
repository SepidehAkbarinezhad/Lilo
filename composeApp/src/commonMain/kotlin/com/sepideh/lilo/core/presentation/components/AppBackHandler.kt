package com.sepideh.lilo.core.presentation.components
import androidx.compose.runtime.Composable
@Composable expect fun AppBackHandler(enabled: Boolean = true, onBack: () -> Unit)
