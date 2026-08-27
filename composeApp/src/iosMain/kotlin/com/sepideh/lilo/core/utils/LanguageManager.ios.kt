package com.sepideh.lilo.core.utils

import platform.Foundation.NSUserDefaults

actual class LanguageManager {
    actual suspend fun applyLanguage(language: String) {
        val userDefaults = NSUserDefaults.standardUserDefaults
        userDefaults.setObject(listOf(language), "AppleLanguages")
        userDefaults.synchronize()
    }
}