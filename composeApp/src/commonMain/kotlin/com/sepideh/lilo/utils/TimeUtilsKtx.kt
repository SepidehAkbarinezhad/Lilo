package com.sepideh.lilo.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getCurrentTime(): Pair<Int, Int> {
    val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = currentDateTime.hour
    val minute = currentDateTime.minute
    return Pair(hour, minute)
}