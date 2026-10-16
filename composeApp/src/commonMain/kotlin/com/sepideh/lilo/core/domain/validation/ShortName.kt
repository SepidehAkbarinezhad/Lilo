package com.sepideh.lilo.core.domain.validation

const val SHORT_NAME_MAX_LENGTH = 20

fun isShortNameTooLong(value: String): Boolean = value.trim().length > SHORT_NAME_MAX_LENGTH

/** Validate edited names without rejecting unchanged legacy values in the other language. */
fun requireShortNameLength(value: String, previousValue: String? = null) {
    require(value == previousValue || !isShortNameTooLong(value)) { "Name exceeds $SHORT_NAME_MAX_LENGTH characters" }
}
