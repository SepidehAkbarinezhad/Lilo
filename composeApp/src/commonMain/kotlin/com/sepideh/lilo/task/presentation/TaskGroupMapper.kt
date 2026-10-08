package com.sepideh.lilo.task.presentation

import com.sepideh.lilo.task.domain.TaskGroup

import com.sepideh.lilo.task.presentation.TaskGroupUi
import com.sepideh.lilo.core.domain.model.AppLanguage

fun List<TaskGroup>.toPresentationList(language: AppLanguage): List<TaskGroupUi> {
    return map { it.toPresentation(language) }
        .filter { it.title.isNotBlank() }
        .sortedWith(compareBy<TaskGroupUi> { groupAlphabeticalKey(it.title) }.thenBy { it.id })
}

fun TaskGroup.toPresentation(language: AppLanguage): TaskGroupUi {
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
    return TaskGroupUi(id = this.id, title = effectiveTitle, isDeletable = isDeletable, isEditable = !isDefault)
}

/** Case-insensitive English ordering and Persian letter order, including پ، چ، ژ، گ. */
private fun groupAlphabeticalKey(title: String): String {
    val alphabet = "ابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهی"
    return buildString {
        title.trim().lowercase().forEach { raw ->
            val letter = when (raw) {
                'آ', 'أ', 'إ' -> 'ا'
                'ك' -> 'ک'
                'ي', 'ى' -> 'ی'
                else -> raw
            }
            val index = alphabet.indexOf(letter)
            append(if (index >= 0) (0xE000 + index).toChar() else letter)
        }
    }
}
