package com.sepideh.lilo.note.domain.model

data class Note(
    val id: Long = 0,
    val title: String = "",
    val content: String = "",
    val groupId: Long? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val imageNames: List<String> = emptyList(),
) {
    val lastModifiedAt: Long get() = updatedAt.takeIf { it > 0 } ?: createdAt
}
