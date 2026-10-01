package com.sepideh.lilo.category.domain

import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider

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