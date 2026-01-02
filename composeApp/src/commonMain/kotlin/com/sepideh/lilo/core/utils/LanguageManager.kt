package com.sepideh.lilo.core.utils

import androidx.compose.runtime.staticCompositionLocalOf


val LocalLocalization = staticCompositionLocalOf { "en" }
expect fun changeLanguage(language: String)
