package com.sepideh.lilo.app

import androidx.compose.ui.window.ComposeUIViewController
import com.sepideh.lilo.core.di.initKoin
import com.sepideh.lilo.core.notifications.initNotificationDelegate

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    // Set delegate before rendering UI
    initNotificationDelegate()
    App()
}