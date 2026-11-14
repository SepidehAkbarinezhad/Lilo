package com.sepideh.lilo.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.graphics.Color

fun featureTextSelectionColors(accent: Color) = TextSelectionColors(
    handleColor = accent,
    backgroundColor = accent.copy(alpha = .4f),
)
