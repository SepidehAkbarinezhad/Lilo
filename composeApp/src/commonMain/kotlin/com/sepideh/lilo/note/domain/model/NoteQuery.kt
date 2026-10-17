package com.sepideh.lilo.note.domain.model

import com.sepideh.lilo.core.domain.format.alphabeticalKey
import com.sepideh.lilo.core.domain.model.AppLanguage

enum class NoteSort { UPDATED, TITLE }

/** null means all groups; zero means notes with no effective group. */
data class NoteQuery(val text: String = "", val groupId: Long? = null, val sort: NoteSort = NoteSort.UPDATED,
    val language: AppLanguage = AppLanguage.EN)

private fun normalized(text: String) = text.trim().lowercase().replace('ي', 'ی').replace('ك', 'ک')

fun List<Note>.matching(query: NoteQuery): List<Note> {
    val needle = normalized(query.text)
    val filtered = filter { note ->
        (query.groupId == null || (query.groupId == 0L && note.groupId == null) || note.groupId == query.groupId) &&
            (needle.isEmpty() || normalized(note.title).contains(needle) || normalized(note.content).contains(needle))
    }
    return when (query.sort) {
        NoteSort.UPDATED -> filtered.sortedWith(compareByDescending<Note> { it.lastModifiedAt }.thenByDescending { it.id })
        NoteSort.TITLE -> filtered.sortedWith(compareBy<Note> { titleLanguageRank(it.title, query.language) }
            .thenBy { alphabeticalKey(it.title) }.thenBy { it.id })
    }
}

/** The first letter determines the script; leading spaces, numbers and symbols do not. */
private fun titleLanguageRank(title: String, language: AppLanguage): Int {
    val firstLetter = title.firstOrNull { it.isLetter() } ?: return 2
    val isPersian = firstLetter in '\u0600'..'\u06FF'
    val isEnglish = firstLetter.lowercaseChar() in 'a'..'z'
    return when {
        language == AppLanguage.FA && isPersian || language == AppLanguage.EN && isEnglish -> 0
        isPersian || isEnglish -> 1
        else -> 2
    }
}
