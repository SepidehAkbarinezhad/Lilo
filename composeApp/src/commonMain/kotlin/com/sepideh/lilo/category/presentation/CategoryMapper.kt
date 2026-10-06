package com.sepideh.lilo.category.presentation

import com.sepideh.lilo.category.domain.CategoryDomain

import com.sepideh.lilo.category.GENERAL_CATEGORY
import com.sepideh.lilo.category.presentation.CategoryPresentation
import com.sepideh.lilo.core.domain.model.AppLanguage

fun List<CategoryDomain>.toPresentationList(language: AppLanguage): List<CategoryPresentation> {
    return map { it.toPresentation(language) }
        .filter { it.title.isNotBlank() }
}

fun CategoryDomain.toPresentation(language: AppLanguage): CategoryPresentation {
    // Get title in selected language
    val titleInSelectedLang = when (language) {
        AppLanguage.FA -> titleFa
        AppLanguage.EN -> titleEn
    }

    // Get fallback title from other language
    val fallbackTitle = when (language) {
        AppLanguage.FA -> titleEn
        AppLanguage.EN -> titleFa
    }

    // Determine effective title
    val effectiveTitle = titleInSelectedLang.ifBlank { fallbackTitle }

    val isDeletable = true
    return CategoryPresentation(id = this.id, title = effectiveTitle, isDeletable = isDeletable)
}
