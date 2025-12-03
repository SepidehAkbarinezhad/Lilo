package com.sepideh.lilo.core.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.intl.Locale
import com.sepideh.lilo.settings.presentation.model.AppLanguage

@Composable
expect fun ApplyLanguage(language: AppLanguage)
val LocalAppLocale = compositionLocalOf { Locale("en") }

