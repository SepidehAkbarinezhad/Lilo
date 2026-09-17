package com.sepideh.lilo.core.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

class AndroidLiloInfo(private val context: Context) : LiloInfo {
    override val platformType = PlatformType.ANDROID
    override val appVersion: String
        get() {
            val pm = context.packageManager
            val info = if (Build.VERSION.SDK_INT >= 33) {
                pm.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION") pm.getPackageInfo(context.packageName, 0)
            }
            return info.versionName ?: "1.0.0"
        }
}