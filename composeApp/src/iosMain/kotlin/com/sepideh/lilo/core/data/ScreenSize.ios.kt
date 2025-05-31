package com.sepideh.lilo.core.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.UIKit.UIScreen

actual object ScreenSize {
    @OptIn(ExperimentalForeignApi::class)
    actual val heightDp: Float
        get() = UIScreen.mainScreen.bounds.useContents { size.height.toFloat() }
}