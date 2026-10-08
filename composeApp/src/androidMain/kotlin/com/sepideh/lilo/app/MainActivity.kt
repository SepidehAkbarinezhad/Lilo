package com.sepideh.lilo.app

import android.app.UiModeManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.sepideh.lilo.core.domain.model.AppTheme
import com.sepideh.lilo.settings.domain.usecase.UserPreferencesManager
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform.getKoin
import androidx.annotation.RequiresApi

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val preferences = getKoin().get<UserPreferencesManager>()
            val uiModeManager = getSystemService(UiModeManager::class.java)
            lifecycleScope.launch {
                preferences.userPreferences.map { it.theme }.distinctUntilChanged().collect { theme ->
                    // Android persists this choice for the next system launch screen.
                    uiModeManager.setApplicationNightMode(when (theme) {
                        AppTheme.DARK -> UiModeManager.MODE_NIGHT_YES
                        AppTheme.LIGHT -> UiModeManager.MODE_NIGHT_NO
                        AppTheme.SYSTEM -> UiModeManager.MODE_NIGHT_AUTO
                    })
                }
            }
        }
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

