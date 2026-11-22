package com.sepideh.lilo.task.domain

import com.sepideh.lilo.task.domain.reminder.*
import com.sepideh.lilo.task.data.mapper.toEntity
import com.sepideh.lilo.task.data.mapper.toTask
import com.sepideh.lilo.task.domain.model.Task
import kotlinx.datetime.LocalDateTime
import kotlin.test.*
import kotlin.time.Instant
import java.util.Locale
import java.util.TimeZone

class MonthlyReminderEnvironmentTest {
    @Test fun languageAndDeviceZoneChangesDoNotReinterpretStoredRules() {
        val oldLocale = Locale.getDefault()
        val oldZone = TimeZone.getDefault()
        try {
            for (calendar in ReminderCalendarType.entries) {
                val local = LocalDateTime.parse("2026-01-31T09:15")
                val task = Task(id = 1, title = "Task", reminderAt = Instant.parse("2026-01-31T05:45:00Z").toEpochMilliseconds(),
                    repeatRule = RepeatRule.MONTHLY, reminderTimeZoneId = "Asia/Tehran", monthlyAnchor = MonthlyAnchor(calendar, local))
                val stored = task.toEntity()
                val after = task.reminderAt!!
                val expected = task.asReminder()!!.nextOccurrence(after)
                for (zone in listOf("UTC", "America/New_York", "Pacific/Auckland")) {
                    TimeZone.setDefault(TimeZone.getTimeZone(zone))
                    for (locale in listOf(Locale.ENGLISH, Locale.forLanguageTag("fa"))) {
                        Locale.setDefault(locale)
                        val restored = stored.toTask()
                        assertEquals(task.monthlyAnchor, restored.monthlyAnchor)
                        assertEquals(expected, restored.asReminder()!!.nextOccurrence(after))
                    }
                }
            }
        } finally { Locale.setDefault(oldLocale); TimeZone.setDefault(oldZone) }
    }
}
