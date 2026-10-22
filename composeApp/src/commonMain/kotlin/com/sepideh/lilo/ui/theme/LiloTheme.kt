package com.sepideh.lilo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.sepideh.lilo.core.domain.model.AppLanguage
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.sepideh.lilo.core.domain.model.AppTheme
import com.sepideh.lilo.core.utils.LocalizedContent
import com.sepideh.lilo.core.utils.LanguageUtils
import com.sepideh.lilo.core.utils.SystemBarAppearance
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import org.koin.mp.KoinPlatform.getKoin


val LocalLiloAppLanguage = staticCompositionLocalOf { AppLanguage.EN }

/** Saved appearance and language are read together before drawing the app. */
@Composable
internal fun LiloTheme(
    content: @Composable () -> Unit
) {
    val userPreferencesManager: UserPreferencesManager = remember { getKoin().get() }
    val loadedPreferences by userPreferencesManager.appearancePreferences.collectAsState(initial = null)
    // Keep the native first frame visible until the saved appearance is known.
    val userPreferences = loadedPreferences ?: return
    val darkTheme = when (userPreferences.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }


    SystemBarAppearance(darkTheme)
    LocalizedContent(userPreferences.language.code) {
        LiloTheme(
            darkTheme = darkTheme,
            layoutDirection = LanguageUtils.layoutDirection(userPreferences.language.code),
            appLanguage = userPreferences.language,
            content = content
        )
    }
}

//  Pure, parameterized version: used by previews & tests, no Koin needed, no runtime process
@Composable
internal fun LiloTheme(
    darkTheme: Boolean,
    layoutDirection: LayoutDirection,
    appLanguage: AppLanguage = if (layoutDirection == LayoutDirection.Rtl) AppLanguage.FA else AppLanguage.EN,
    content: @Composable () -> Unit
) {

    val liloColorsPalette = if (darkTheme) LiloColorsDark else LiloColorsLight
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme


    CompositionLocalProvider(
        LocalLiloColorsPalette provides liloColorsPalette,
        LocalLayoutDirection provides layoutDirection,
        LocalLiloAppLanguage provides appLanguage,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = liloTypography(),
            shapes = LiloShapes,
            content = content
        )
    }
}

object LiloExtendedTheme {
    val colors: LiloColors
        @Composable get() = LocalLiloColorsPalette.current
}