package com.sepideh.lilo.core.utils

import java.util.Locale

actual fun changeLanguage(language: String) {
    println("changeLanguage ddffdd $language")

    val locale = Locale(language)
    Locale.setDefault(locale)
}
