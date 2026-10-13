package com.sepideh.lilo.core.domain.validation

class DuplicateNameException : IllegalArgumentException("A name already exists")

/** Names differing only in outer whitespace or letter case are the same name. */
fun requireUniqueName(name: String, existingNames: Iterable<String>) {
    val normalized = name.trim()
    require(normalized.isNotEmpty())
    if (existingNames.any { it.trim().equals(normalized, ignoreCase = true) }) {
        throw DuplicateNameException()
    }
}
