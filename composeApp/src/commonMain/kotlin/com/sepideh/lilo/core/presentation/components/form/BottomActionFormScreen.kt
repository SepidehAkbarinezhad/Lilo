package com.sepideh.lilo.core.presentation.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.sepideh.lilo.core.presentation.BaseHeader
import org.jetbrains.compose.resources.StringResource

@Composable
fun BottomActionFormScreen(
    title: StringResource,
    accent: Color,
    saveEnabled: Boolean,
    isSaving: Boolean,
    actionLabel: StringResource,
    onBack: () -> Boolean,
    onSave: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            BaseHeader(
                title = title,
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
                onBackPressed = onBack,
            )
        },
        bottomBar = {
            // Do not attach the action to the keyboard. Fields handle their own IME padding.
            if (!keyboardVisible) FormSaveButton(accent, actionLabel, saveEnabled, isSaving, onSave)
        },
        content = content,
    )
}
