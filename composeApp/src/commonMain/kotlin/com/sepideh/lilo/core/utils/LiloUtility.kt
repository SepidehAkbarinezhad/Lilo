package com.sepideh.lilo.core.utils

enum class PlatformType {
    ANDROID, IOS
}
expect fun getPlatformType(): PlatformType
expect fun isPersianLanguage(): Boolean