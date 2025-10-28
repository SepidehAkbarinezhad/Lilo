package com.sepideh.lilo.core.utils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode


actual fun getPlatformType(): PlatformType = PlatformType.IOS
actual fun isPersianLanguage(): Boolean {
    return NSLocale.currentLocale.languageCode == "fa" ?: false
}
