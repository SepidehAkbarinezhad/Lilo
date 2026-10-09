package com.sepideh.lilo.note.domain.model

enum class NoteSort { UPDATED, TITLE }

/** null means all groups; zero means notes with no effective group. */
data class NoteQuery(val text: String = "", val groupId: Long? = null, val sort: NoteSort = NoteSort.UPDATED)

private fun normalized(text: String) = text.trim().lowercase().replace('ي', 'ی').replace('ك', 'ک')

fun List<Note>.matching(query: NoteQuery): List<Note> {
    val needle = normalized(query.text)
    val filtered = filter { note ->
        (query.groupId == null || (query.groupId == 0L && note.groupId == null) || note.groupId == query.groupId) &&
            (needle.isEmpty() || normalized(note.title).contains(needle) || normalized(note.content).contains(needle))
    }
    return when (query.sort) {
        NoteSort.UPDATED -> filtered.sortedWith(compareByDescending<Note> { it.updatedAt }.thenByDescending { it.id })
        NoteSort.TITLE -> filtered.sortedWith(compareBy<Note> { normalized(it.title) }.thenBy { it.id })
    }
}
