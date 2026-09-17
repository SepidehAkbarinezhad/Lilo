package com.sepideh.lilo.core.utils

enum class PlatformType { ANDROID, IOS }

interface LiloInfo {
    val platformType: PlatformType
    val appVersion: String
}