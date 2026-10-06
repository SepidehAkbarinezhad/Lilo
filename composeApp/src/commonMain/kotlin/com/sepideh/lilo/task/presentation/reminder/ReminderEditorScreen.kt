package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseFormScreen
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.picker.TimeWheelPicker
import com.sepideh.lilo.core.presentation.components.picker.ReminderCalendar
import com.sepideh.lilo.task.domain.reminder.RepeatRule
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.Instant
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import com.sepideh.lilo.ui.theme.*

val RepeatRule.label: StringResource get() = when (this) {
    RepeatRule.NONE -> Res.string.repeat_once
    RepeatRule.DAILY -> Res.string.repeat_daily
    RepeatRule.WEEKLY -> Res.string.repeat_weekly
}

/** Editing is local; only Confirm updates the task form. Scheduling happens on Save. */
@Composable
fun ReminderEditorScreen(
    reminderAt: Long?, repeatRule: RepeatRule, timeZoneId: String?, accent: Color,
    onConfirm: (Long, RepeatRule, String) -> Unit, onDismiss: () -> Unit,
) {
    val zoneId = rememberSaveable { timeZoneId ?: TimeZone.currentSystemDefault().id }
    val zone = remember(zoneId) { TimeZone.of(zoneId) }
    val initial = remember { Instant.fromEpochMilliseconds(reminderAt ?: Clock.System.now().toEpochMilliseconds()).toLocalDateTime(zone) }
    var day by rememberSaveable { mutableStateOf(initial.date.toString()) }
    var hours by rememberSaveable { mutableStateOf(initial.hour) }
    var minutes by rememberSaveable { mutableStateOf(initial.minute) }
    var repeatCode by rememberSaveable { mutableStateOf(repeatRule.name) }
    var error by remember { mutableStateOf(false) }
    val selectedRepeat = RepeatRule.fromCode(repeatCode)
    val date = LocalDate.parse(day)
    val hour = hours
    val minute = minutes
    val valid = hour in 0..23 && minute in 0..59
    BaseFormScreen(title = Res.string.reminder_label, accent = accent, saveEnabled = valid,
        actionLabel = Res.string.confirm_action, textSaveAction = true, onBack = { onDismiss(); true }, onSave = {
            if (valid) {
                val at = LocalDateTime(date, LocalTime(hour, minute)).toInstant(zone).toEpochMilliseconds()
                // Existing repeats can retain their original anchor. New/edited starts must be future.
                if (at <= Clock.System.now().toEpochMilliseconds() && !(at == reminderAt && selectedRepeat != RepeatRule.NONE)) error = true
                else onConfirm(at, selectedRepeat, zoneId)
            }
        }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Box(Modifier.fillMaxWidth().height(440.dp * androidx.compose.ui.platform.LocalDensity.current.fontScale)) {
                ReminderCalendar(date, accent, onDateChange = { day = it.toString(); error = false })
            }
            AppText(text = Res.string.reminder_time_title, textType = TextType.SectionTitle)
            TimeWheelPicker(hours, minutes, accent,
                onHourChange = { hours = it; error = false },
                onMinuteChange = { minutes = it; error = false })
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppText(text = Res.string.repeat_label, textType = TextType.SectionTitle)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
                    RepeatRule.entries.forEach { rule ->
                        LiloSelectionChip(stringResource(rule.label), selectedRepeat == rule, accent,
                            { repeatCode = rule.name; error = false }, modifier = Modifier.weight(1f))
                    }
                }
                if (selectedRepeat == RepeatRule.WEEKLY) {
                    val weekdays = listOf(Res.string.weekday_mon, Res.string.weekday_tue, Res.string.weekday_wed,
                        Res.string.weekday_thu, Res.string.weekday_fri, Res.string.weekday_sat, Res.string.weekday_sun)
                    AppText(text = stringResource(Res.string.repeat_every_weekday, stringResource(weekdays[date.dayOfWeek.ordinal])),
                        textType = TextType.Caption, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (error) AppText(text = Res.string.reminder_future_error, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(name = "Reminder", showBackground = true, widthDp = 390, heightDp = 850)
@Composable
private fun ReminderEditorPreview() {
    LiloPreviewWrapper { ReminderEditorScreen(null, RepeatRule.NONE, null, Color(0xFFFFC107), { _, _, _ -> }, {}) }
}
