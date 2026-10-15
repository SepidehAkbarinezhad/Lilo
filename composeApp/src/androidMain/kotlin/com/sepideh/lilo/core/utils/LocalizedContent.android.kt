package com.sepideh.lilo.core.utils

import android.content.res.Configuration
import android.view.ContextThemeWrapper
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
actual fun LocalizedContent(languageCode: String, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val localized = remember(context, configuration, languageCode) {
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)
        ContextThemeWrapper(context, 0).apply {
            applyOverrideConfiguration(Configuration(configuration).apply { setLocale(locale) })
        }
    }
    CompositionLocalProvider(
        LocalContext provides localized,
        LocalConfiguration provides localized.resources.configuration,
    ) {
        key(languageCode) { content() }
    }
}
