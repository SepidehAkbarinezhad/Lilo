package com.sepideh.lilo.app

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.navigation.compose.rememberNavController
import com.sepideh.lilo.app.navigation.NavigationGraph
import com.sepideh.lilo.core.utils.LocalLocalization
import com.sepideh.lilo.core.utils.changeLanguage
import com.sepideh.lilo.settings.domain.model.UserPreferences
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import com.sepideh.lilo.settings.presentation.model.AppTheme
import com.sepideh.lilo.ui.theme.LiloTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import org.koin.mp.KoinPlatform.getKoin


@Composable
fun App() {

    val userPreferencesManager: UserPreferencesManager = remember { getKoin().get() }
    val userPreferences by userPreferencesManager.userPreferences.collectAsState(UserPreferences())


    println("sepidtag $userPreferences   ${userPreferences.language.code} ")

    val darkTheme = when (userPreferences.theme) {
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
        AppTheme.SYSTEM -> isSystemInDarkTheme()
    }
    LaunchedEffect(Unit) {
        println("sepidtag LaunchedEffect   ${userPreferences.language.code} ")
        snapshotFlow { userPreferences.language.code }
            .distinctUntilChanged()
            .collect { code ->
                println("sepidtag collect   ${userPreferences.language.code}")
                changeLanguage(code)
            }
    }

    CompositionLocalProvider(LocalLocalization provides userPreferences.language.code) {
        SideEffect {
            println("🔥sepidtag App recomposed with   ${userPreferences.language.code}")
        }
        LiloTheme(darkTheme = darkTheme, languageCode = userPreferences.language.code) {
            val navHostController = rememberNavController()
            NavigationGraph(navHostController = navHostController)
        }
    }
}


