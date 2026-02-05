package com.sepideh.lilo.core.utils

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.LayoutDirection
import com.sepideh.lilo.settings.presentation.model.AppLanguage


val LocalLanguageCode = staticCompositionLocalOf {AppLanguage.FA.code }
expect fun changeLanguage(language: String)


object LanguageUtils {

    private val rtlLanguages = setOf(AppLanguage.FA.code)

    fun layoutDirection(languageCode: String): LayoutDirection =
        if (isRtl(languageCode)) LayoutDirection.Rtl else LayoutDirection.Ltr

    fun isRtl(languageCode: String): Boolean {

        return languageCode.lowercase() in rtlLanguages
    }
}