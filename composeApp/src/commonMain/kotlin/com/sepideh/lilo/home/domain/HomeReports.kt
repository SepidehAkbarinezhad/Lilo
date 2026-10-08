package com.sepideh.lilo.home.domain

import kotlinx.datetime.toLocalDateTime
import com.sepideh.lilo.task.domain.reminder.asReminder
import com.sepideh.lilo.task.domain.reminder.nextOccurrence
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.home.presentation.model.*

@OptIn(kotlin.time.ExperimentalTime::class)
internal fun taskHomeReport(
    tasks: List<Task>,
    nowMillis: Long = kotlin.time.Clock.System.now().toEpochMilliseconds(),
    zone: kotlinx.datetime.TimeZone = kotlinx.datetime.TimeZone.currentSystemDefault(),
): TaskReportDetail {
    val remaining = tasks.filterNot { it.done }
    val today = kotlin.time.Instant.fromEpochMilliseconds(nowMillis).toLocalDateTime(zone).date
    val nextToday = remaining.mapNotNull { task ->
        val occurrence = runCatching { task.asReminder()?.nextOccurrence(nowMillis - 1) }.getOrNull()
        occurrence?.takeIf { kotlin.time.Instant.fromEpochMilliseconds(it).toLocalDateTime(zone).date == today }
            ?.let { task to it }
    }.minByOrNull { it.second }
    val selected = nextToday?.first ?: remaining.maxByOrNull { it.createdAt }
    val time = nextToday?.second?.let {
        val local = kotlin.time.Instant.fromEpochMilliseconds(it).toLocalDateTime(zone)
        "${local.hour.toString().padStart(2, '0')}:${local.minute.toString().padStart(2, '0')}"
    }
    return TaskReportDetail(selected?.title, time, remaining.size, tasks.size, selected?.id)
}

internal fun noteHomeReport(notes: List<Note>): NoteReportDetail {
    val latest = notes.maxByOrNull { it.updatedAt }
    return NoteReportDetail(latest?.title.orEmpty(), latest?.content.orEmpty(), notes.size, null, notes.size)
}
