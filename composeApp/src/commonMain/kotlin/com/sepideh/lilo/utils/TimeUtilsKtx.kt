package com.sepideh.lilo.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

fun getCurrentTime(): Pair<Int, Int> {
    val currentDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val hour = currentDateTime.hour
    val minute = currentDateTime.minute
    return Pair(hour, minute)
}

fun setReminderTime(dayMillis: Long?, hour: Int?, minute: Int?): Long? {
    if (dayMillis == null || hour == null || minute == null) return null

    val timeZone = TimeZone.currentSystemDefault()

    val localDateTime = Instant.fromEpochMilliseconds(dayMillis)
        .toLocalDateTime(timeZone)

    val updatedDateTime = LocalDateTime(
        year = localDateTime.year,
        month = localDateTime.month,
        dayOfMonth = localDateTime.dayOfMonth,
        hour = hour,
        minute = minute,
        second = 0,
        nanosecond = 0
    )

    return updatedDateTime.toInstant(timeZone).toEpochMilliseconds()
}



