package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.format.localizedDigits
import com.sepideh.lilo.task.domain.model.Task
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import io.github.faridsolgi.persiandatetime.extensions.toPersianDateTime
import io.github.faridsolgi.persiandatetime.domain.PersianMonth
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant
import kotlin.time.ExperimentalTime
import org.jetbrains.compose.resources.stringResource
import lilo.composeapp.generated.resources.*

/** Tolerates old rows whose reminder zone is absent rather than crashing either screen. */
@OptIn(ExperimentalTime::class)
@Composable
fun taskReminderLabel(task: Task, includeRepeat: Boolean = false): String {
    val at = task.reminderAt ?: return stringResource(Res.string.not_set_label)
    val zone = task.reminderTimeZoneId?.let { runCatching { TimeZone.of(it) }.getOrNull() } ?: TimeZone.currentSystemDefault()
    val dateTime = Instant.fromEpochMilliseconds(at).toLocalDateTime(zone)
    val persian = LocalLayoutDirection.current == LayoutDirection.Rtl
    val today = Clock.System.now().toLocalDateTime(zone).date
    val dateLabel = when (dateTime.date.toEpochDays().toLong() - today.toEpochDays().toLong()) {
        0L -> stringResource(Res.string.task_today)
        1L -> stringResource(Res.string.task_tomorrow)
        else -> if (persian) {
            val date = dateTime.toPersianDateTime()
            "${date.day} ${PersianMonth.entries[date.month].displayName}"
        } else dateTime.date.toString()
    }
    val repeat = if (includeRepeat && task.repeatRule != com.sepideh.lilo.task.domain.reminder.RepeatRule.NONE) " · ${stringResource(task.repeatRule.label)}" else ""
    val time = "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
    val label = "$dateLabel, \u2066$time\u2069$repeat"
    return label.localizedDigits(persian)
}
