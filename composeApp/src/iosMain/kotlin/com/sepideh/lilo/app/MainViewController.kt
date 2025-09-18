package com.sepideh.lilo.app

import androidx.compose.ui.window.ComposeUIViewController
import com.sepideh.lilo.core.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {

    App()
}