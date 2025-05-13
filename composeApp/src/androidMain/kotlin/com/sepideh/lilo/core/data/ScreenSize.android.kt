package com.sepideh.lilo.core.data

import android.content.res.Resources

actual object ScreenSize {
    actual val heightDp: Float
        get() = Resources.getSystem().displayMetrics.run { heightPixels / density }
    actual val widthDp: Float
        get() = Resources.getSystem().displayMetrics.run { widthPixels / density }
}