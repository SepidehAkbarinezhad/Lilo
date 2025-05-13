package com.sepideh.lilo.core.data

actual object ScreenSize {
    actual val widthDp: Float
        get() = UIScreen.mainScreen.bounds.useContents { size.width.toFloat() }

    actual val heightDp: Float
        get() = UIScreen.mainScreen.bounds.useContents { size.height.toFloat() }
}