package com.sepideh.lilo.core.utils

import java.util.Locale

actual fun getPlatformType(): PlatformType = PlatformType.ANDROID
actual fun isPersianLanguage(): Boolean {
    val locale = Locale.getDefault().language
    return locale.startsWith("fa", ignoreCase = true)
}

