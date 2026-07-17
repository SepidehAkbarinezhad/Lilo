package com.sepideh.lilo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import com.sepideh.lilo.core.utils.LanguageUtils

val DarkColorScheme = darkColorScheme(
    background = Color(0xFF121212),
    surface = Grey900,
    surfaceVariant = Grey800,
    surfaceContainer = BlueGray900,
    primary = Amber600,
    onPrimary = White,
    primaryContainer = Grey800,
    secondary = Blue600,
)
val LightColorScheme = lightColorScheme(
    background = White,
    surface = White,
    onSurface = Gray600,
    surfaceVariant = Color(0x0DFFAB00),
    onSurfaceVariant = Grey900,
    surfaceContainer = White,
    primary = Amber600,
    onPrimary = White,
    primaryContainer = Amber500,
    secondary = Blue600,
)

@Composable
internal fun LiloTheme(
    darkTheme: Boolean,
    languageCode: String,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val liloColorsPalette = if (darkTheme) LiloColorsDark else LiloColorsLight

    val layoutDirection = remember(languageCode) {
        LanguageUtils.layoutDirection(languageCode)
    }

    CompositionLocalProvider(
        LocalLiloColorsPalette provides liloColorsPalette,
        LocalLayoutDirection provides layoutDirection,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }

}

object AppTheme {
    val liloColor: LiloColors
        @Composable get() = LocalLiloColorsPalette.current
}