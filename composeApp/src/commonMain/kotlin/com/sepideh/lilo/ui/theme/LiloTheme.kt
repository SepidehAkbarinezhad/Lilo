package com.sepideh.lilo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import com.sepideh.lilo.core.utils.LanguageUtils
import com.sepideh.lilo.core.utils.LocalLanguageCode
import com.sepideh.lilo.core.utils.changeLanguage
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import com.sepideh.lilo.settings.presentation.model.AppTheme
import org.koin.mp.KoinPlatform.getKoin

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

// --- Real app entry point: reads live UserPreferences from Koin ---
@Composable
internal fun LiloTheme(
    content: @Composable () -> Unit
) {
    val userPreferencesManager: UserPreferencesManager = remember { getKoin().get() }
    val userPreferences by userPreferencesManager.userPreferences.collectAsState(UserPreferences())

    val darkTheme = when (userPreferences.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }

    LiloTheme(
        darkTheme = darkTheme,
        languageCode = userPreferences.language.code,
        content = content
    )
}

// --- Pure, parameterized version: used by previews & tests, no Koin needed ---
@Composable
internal fun LiloTheme(
    darkTheme: Boolean,
    languageCode: String,
    content: @Composable () -> Unit
) {

    val layoutDirection = remember(languageCode) {
        LanguageUtils.layoutDirection(languageCode)
    }

    val liloColorsPalette = if (darkTheme) LiloColorsDark else LiloColorsLight
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme


    CompositionLocalProvider(
        LocalLiloColorsPalette provides liloColorsPalette,
        LocalLayoutDirection provides layoutDirection,
        LocalLanguageCode provides languageCode
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object LiloAppTheme {
    val liloColor: LiloColors
        @Composable get() = LocalLiloColorsPalette.current
}