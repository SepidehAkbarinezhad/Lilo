package com.sepideh.lilo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

val DarkColorScheme = darkColorScheme(
    background = BackGroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceContainer = SurfaceContainerDark,
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    tertiary = TertiaryDark,
)
val LightColorScheme = lightColorScheme(
    background = BackGroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceContainer = SurfaceContainerLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    tertiary = TertiaryLight,
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