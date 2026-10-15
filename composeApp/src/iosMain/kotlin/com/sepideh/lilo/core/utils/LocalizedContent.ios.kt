package com.sepideh.lilo.core.utils

import androidx.compose.runtime.*
import platform.Foundation.NSUserDefaults

@Composable
actual fun LocalizedContent(languageCode: String, content: @Composable () -> Unit) {
    remember(languageCode) {
        NSUserDefaults.standardUserDefaults.setObject(listOf(languageCode), forKey = "AppleLanguages")
    }
    key(languageCode) { content() }
}
