package com.sepideh.lilo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

@Immutable
data class FeatureChromeColors(val container: Color, val content: Color)

/** Shared by home cards, list headers and form actions so their fills remain identical. */
@Composable
fun featureChromeColors(accent: Color): FeatureChromeColors {
    val background = MaterialTheme.colorScheme.background
    val dark = background.luminance() < .5f
    val tint = when {
        dark -> .16f
        accent == LiloExtendedTheme.colors.passwordColor -> .30f
        else -> .24f
    }
    return FeatureChromeColors(
        container = lerp(background, accent, tint),
        content = if (dark) LiloExtendedTheme.colors.title else LiloExtendedTheme.colors.onAccent,
    )
}
