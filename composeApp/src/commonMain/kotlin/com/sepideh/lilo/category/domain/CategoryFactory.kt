package com.sepideh.lilo.category.domain

import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import com.sepideh.lilo.settings.presentation.model.AppLanguage

class CategoryFactory(
    private val languageProvider: LanguageProvider
) {
    fun create(title: String): CategoryDomain {
        return if (languageProvider.currentLanguage == AppLanguage.FA) {
            CategoryDomain(titleFa = title, titleEn = "")
        } else {
            CategoryDomain(titleEn = title, titleFa = "")
        }
    }
}