package com.sepideh.lilo.core.presentation.components.form

import com.sepideh.lilo.ui.theme.FeatureColors
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseHeader
import org.jetbrains.compose.resources.StringResource

@Composable
fun BottomActionFormScreen(
    title: StringResource,
    featureColors: FeatureColors,
    saveEnabled: Boolean,
    isSaving: Boolean,
    actionLabel: StringResource,
    onBack: () -> Boolean,
    onSave: () -> Unit,
    headerColors: FeatureColors? = featureColors,
    content: @Composable (PaddingValues) -> Unit,
) {
    val keyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Box(Modifier.clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))) {
                BaseHeader(
                    title = title,
                    featureColors = headerColors,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
                    onBackPressed = onBack,
                )
            }
        },
        bottomBar = {
            // Do not attach the action to the keyboard. Fields handle their own IME padding.
            if (!keyboardVisible) FormSaveButton(featureColors, actionLabel, saveEnabled, isSaving, onSave)
        },
        content = content,
    )
}
