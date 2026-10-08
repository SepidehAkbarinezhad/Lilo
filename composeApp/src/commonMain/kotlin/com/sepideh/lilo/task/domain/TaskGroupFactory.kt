package com.sepideh.lilo.task.domain

import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider

class TaskGroupFactory(
    private val languageProvider: LanguageProvider
) {
    fun create(title: String): TaskGroup {
        return if (languageProvider.currentLanguage == AppLanguage.FA) {
            TaskGroup(titleFa = title, titleEn = "")
        } else {
            TaskGroup(titleEn = title, titleFa = "")
        }
    }
}