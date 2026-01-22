package com.sepideh.lilo.core.utils

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.LayoutDirection
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import com.sepideh.lilo.settings.presentation.model.AppLanguage


val LocalLocalization = staticCompositionLocalOf { "en" }
expect fun changeLanguage(language: String)


object LanguageUtils {

    private val rtlLanguages = setOf(AppLanguage.FA.code)

    lateinit var languageProvider: LanguageProvider

    fun isRtl(): Boolean {
        return languageProvider.currentLanguage.code.lowercase() in rtlLanguages
    }

    fun layoutDirection(): LayoutDirection =
        if (isRtl()) LayoutDirection.Rtl else LayoutDirection.Ltr
}