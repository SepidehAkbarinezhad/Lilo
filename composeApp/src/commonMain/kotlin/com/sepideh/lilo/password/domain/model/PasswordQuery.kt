package com.sepideh.lilo.password.domain.model

fun List<Password>.matching(query: String, alphabetical: Boolean): List<Password> {
    val text = query.trim().lowercase().replace('ي', 'ی').replace('ك', 'ک')
    fun normalized(value: String) = value.lowercase().replace('ي', 'ی').replace('ك', 'ک')
    val matches = filter { text.isEmpty() || normalized(it.displayTitle).contains(text) || normalized(it.serviceName).contains(text) || normalized(it.serviceNameFa).contains(text) || normalized(it.username).contains(text) || normalized(it.website).contains(text) }
    return if (alphabetical) matches.sortedWith(compareBy<Password> { normalized(it.displayTitle) }.thenBy { it.id })
    else matches.sortedWith(compareByDescending<Password> { it.updatedAt }.thenBy { it.id })
}
