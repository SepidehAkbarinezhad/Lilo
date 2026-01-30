package com.sepideh.lilo.category.domain

import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.category.presentation.CategoryPresentation

fun List<CategoryDomain>.toPresentationList(language: AppLanguage): List<CategoryPresentation> {
    return when (language) {
        AppLanguage.FA -> filter { it.titleFa.isNotBlank() }
        AppLanguage.EN -> filter { it.titleEn.isNotBlank() }
    }.map {
        it.toPresentation(language)
    }
}

fun CategoryDomain.toPresentation(language: AppLanguage): CategoryPresentation =
    when (language) {
        AppLanguage.FA -> CategoryPresentation(id = this.id, title = this.titleFa)
        AppLanguage.EN -> CategoryPresentation(id = this.id, title = this.titleEn)
    }

