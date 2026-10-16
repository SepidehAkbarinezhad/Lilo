package com.sepideh.lilo.app

import androidx.compose.ui.window.ComposeUIViewController
import com.sepideh.lilo.core.di.initKoin
import com.sepideh.lilo.core.notifications.initNotificationDelegate

private var initialized = false

/** Install notification callbacks during app startup, before iOS delivers a response. */
fun initializeLilo() {
    if (initialized) return
    initKoin()
    initNotificationDelegate()
    initialized = true
}

fun MainViewController() = ComposeUIViewController(
    configure = {
        initializeLilo()
    }
) {
    App()
}