package com.sepideh.lilo.core.presentation.format

/** Numeric formatting only; never changes stored values or ordering. */
fun String.localizedDigits(persian: Boolean): String = if (persian) map { char ->
    if (char in '0'..'9') "۰۱۲۳۴۵۶۷۸۹"[char - '0'] else char
}.joinToString("") else this
