package com.sepideh.lilo.core.utils

import platform.Foundation.NSLocale

actual fun getSystemLanguage(): String {
    return NSLocale.currentLocale.languageCode ?: "en"
}