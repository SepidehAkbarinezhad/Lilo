package com.sepideh.lilo.core.utils

import platform.Foundation.NSUserDefaults

actual fun changeLanguage(language: String) {
    NSUserDefaults.standardUserDefaults
        .setObject(arrayListOf(language), "AppleLanguages")
}