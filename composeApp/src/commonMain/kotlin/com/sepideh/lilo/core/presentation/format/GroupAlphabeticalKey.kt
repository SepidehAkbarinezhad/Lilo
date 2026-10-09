package com.sepideh.lilo.core.presentation.format

/** Case-insensitive English ordering and Persian letter order, including پ، چ، ژ، گ. */
fun groupAlphabeticalKey(title: String): String {
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
