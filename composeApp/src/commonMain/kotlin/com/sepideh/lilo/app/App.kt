package com.sepideh.lilo.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.core.utils.LocalLanguageCode
import com.sepideh.lilo.core.utils.changeLanguage
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import com.sepideh.lilo.settings.presentation.model.AppTheme
import com.sepideh.lilo.ui.theme.LiloTheme
import org.koin.mp.KoinPlatform.getKoin


@Composable
fun App() {

    val userPreferencesManager: UserPreferencesManager = remember { getKoin().get() }
    val userPreferences by userPreferencesManager.userPreferences.collectAsState(UserPreferences())
    val languageCode by produceState(initialValue = UserPreferences().language.code) {
        userPreferencesManager.userPreferences.collect { prefs ->
            value = prefs.language.code
            changeLanguage(value) // runs after value updated
        }
    }

    val darkTheme = when (userPreferences.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }


    CompositionLocalProvider(LocalLanguageCode provides languageCode) {
        LiloTheme(darkTheme = darkTheme, languageCode = userPreferences.language.code) {
            val navHostController = rememberNavController()
            NavigationGraph(navHostController = navHostController)
        }
    }
}


