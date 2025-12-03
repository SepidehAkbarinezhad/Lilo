package com.sepideh.lilo.category.presentation

import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.category.domain.model.Category

fun List<Category>.visibleFor(language: AppLanguage): List<Category> {
    return when (language) {
        AppLanguage.FA -> filter { it.titleFa.isNotBlank() }
        AppLanguage.EN -> filter { it.titleEn.isNotBlank() }
    }
}