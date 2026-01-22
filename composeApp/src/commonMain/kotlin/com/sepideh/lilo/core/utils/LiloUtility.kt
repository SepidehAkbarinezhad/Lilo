package com.sepideh.lilo.core.utils

enum class PlatformType {
    ANDROID, IOS
}
expect fun getPlatformType(): PlatformType

fun isRtlLanguage(languageCode: String): Boolean {
    return languageCode in listOf(
        "fa",
    )
}
