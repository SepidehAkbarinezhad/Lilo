package com.sepideh.lilo.core.utils

import java.util.Locale

actual fun changeLanguage(language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)
}
