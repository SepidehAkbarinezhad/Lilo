package com.sepideh.lilo.core.presentation.format

import io.github.faridsolgi.persiandatetime.extensions.toPersianDateTime
import io.github.faridsolgi.persiandatetime.domain.PersianMonth
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun creationDateLabel(timestamp: Long, persian: Boolean, zone: TimeZone = TimeZone.currentSystemDefault()): String? {
    if (timestamp <= 0) return null
    return runCatching {
        val dateTime = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(zone)
        val date = if (persian) {
            val value = dateTime.toPersianDateTime()
            "${value.day} ${PersianMonth.entries[value.month].displayName} ${value.year}"
        } else dateTime.date.toString()
        date.localizedDigits(persian)
    }.getOrNull()
}
