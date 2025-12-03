package com.sepideh.lilo.category.domain.factory

import com.sepideh.lilo.category.domain.model.Category
import com.sepideh.lilo.settings.domain.LanguageProvider
import com.sepideh.lilo.settings.presentation.model.AppLanguage

class CategoryFactory(
    private val languageProvider: LanguageProvider
) {
    fun create(title: String): Category {
        return if (languageProvider.currentLanguage == AppLanguage.FA) {
            Category(titleFa = title, titleEn = "")
        } else {
            Category(titleEn = title, titleFa = "")
        }
    }
}

