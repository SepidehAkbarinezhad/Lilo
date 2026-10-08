package com.sepideh.lilo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import com.sepideh.lilo.core.domain.model.AppLanguage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.sepideh.lilo.core.domain.model.AppTheme
import com.sepideh.lilo.core.utils.LanguageManager
import com.sepideh.lilo.core.utils.LanguageUtils
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import org.koin.mp.KoinPlatform.getKoin


val LocalLiloAppLanguage = staticCompositionLocalOf { AppLanguage.EN }

/*
* Real app entry point:
* reads live UserPreferences from Koin, run side effects,changeLanguage() updates platform process resources at runtime ...
* so cant be called from preview
* */
@Composable
internal fun LiloTheme(
    content: @Composable () -> Unit
) {
    val userPreferencesManager: UserPreferencesManager = remember { getKoin().get() }
    val loadedPreferences by userPreferencesManager.userPreferences.collectAsState(initial = null)
    // Keep the native first frame visible until the saved appearance is known.
    val userPreferences = loadedPreferences ?: return
    val languageManager: LanguageManager = remember { getKoin().get() }
    //todo this should be changed
    val languageCode by produceState(initialValue = UserPreferences().language.code) {
        userPreferencesManager.userPreferences.collect { prefs ->
            value = prefs.language.code
            languageManager.applyLanguage(value) // runs after value updated
        }
    }

    val darkTheme = when (userPreferences.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }


// Apply platform-level changes whenever languageCode updates
    LaunchedEffect(languageCode) {
        languageManager.applyLanguage(languageCode)
    }

    // Derive layout direction directly from the state flow (synchronous with state)
    val layoutDirection = remember(languageCode) {
        LanguageUtils.layoutDirection(languageCode)
    }

        LiloTheme(
            darkTheme = darkTheme,
            layoutDirection = layoutDirection,
            appLanguage = userPreferences.language,
            content = content
        )


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