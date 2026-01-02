package com.sepideh.lilo.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.text.intl.Locale
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.core.utils.LocalAppLocale
import com.sepideh.lilo.core.utils.customAppLocale
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import com.sepideh.lilo.settings.presentation.model.AppTheme
import com.sepideh.lilo.ui.theme.LiloTheme
import org.koin.mp.KoinPlatform.getKoin


@Composable
fun App() {
    val userPreferencesManager: UserPreferencesManager = remember { getKoin().get() }
    val userPreferences by userPreferencesManager.userPreferences.collectAsState(UserPreferences())
    val languagePreference = userPreferences.language
    val darkTheme = when (userPreferences.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }
    LaunchedEffect(languagePreference) {
        customAppLocale = languagePreference.code
    }

    CompositionLocalProvider(
        LocalAppLocale provides customAppLocale,
    ) {
        key(customAppLocale) {
            LiloTheme(darkTheme = darkTheme) {
                val navHostController = rememberNavController()
                NavigationGraph(navHostController = navHostController)
            }
        }
    }

}
