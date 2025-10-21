package com.sepideh.lilo.core.utils

actual fun getSystemLanguage(): String {
    return java.util.Locale.getDefault().language
}