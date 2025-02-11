package com.sepideh.lilo

import androidx.compose.ui.window.ComposeUIViewController
import com.sepideh.lilo.app.App
import com.sepideh.lilo.di.initKoin
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) {
    val darkTheme =
        UIScreen.mainScreen.traitCollection.userInterfaceStyle == UIUserInterfaceStyle.UIUserInterfaceStyleDark
    App(darkTheme = darkTheme, dynamicColor = false)
}