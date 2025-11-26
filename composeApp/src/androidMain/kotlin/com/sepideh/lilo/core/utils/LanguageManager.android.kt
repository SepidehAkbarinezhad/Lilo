package com.sepideh.lilo.core.utils

import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import com.sepideh.lilo.settings.presentation.model.AppLanguage
import java.util.Locale

@Composable
actual fun ApplyLanguage(language: AppLanguage) {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.getSystemService(LocaleManager::class.java).applicationLocales = LocaleList(Locale(language.code))
    }  else {
        // Fallback for older Android versions using AppCompatDelegate
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.code))
    }
}

