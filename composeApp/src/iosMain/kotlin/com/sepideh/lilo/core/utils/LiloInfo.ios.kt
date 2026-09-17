package com.sepideh.lilo.core.utils

import platform.Foundation.NSBundle

class IosLiloInfo : LiloInfo {
    override val platformType = PlatformType.IOS
    override val appVersion: String
        get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String ?: "1.0.0"
}