package com.sepideh.lilo.core.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

actual fun changeLanguage(language: String) {
    val localeList = LocaleListCompat.forLanguageTags(language)
    AppCompatDelegate.setApplicationLocales(localeList)
}
