package com.sepideh.lilo.core.utils

import android.content.Context
import java.util.Locale

actual class LanguageManager(
    private val context: Context
) {
    actual suspend fun applyLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)

        // Update the app resources in-place
        context.createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)

    }
}