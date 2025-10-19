package com.sepideh.lilo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

val DarkColorScheme = darkColorScheme(
    background = BackGroundDark,
    surfaceVariant = SurfaceVariantDark,
    surfaceContainer = SurfaceContainerDark,
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    secondary = SecondaryLight,
)
val LightColorScheme = lightColorScheme(
    background = BackGroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceContainer = SurfaceContainerLight,
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    secondary = SecondaryLight,
)

@Composable
internal fun LiloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val liloColorsPalette = if (darkTheme) LiloColorsDark else LiloColorsLight

    CompositionLocalProvider(LocalLiloColorsPalette provides  liloColorsPalette){
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

}