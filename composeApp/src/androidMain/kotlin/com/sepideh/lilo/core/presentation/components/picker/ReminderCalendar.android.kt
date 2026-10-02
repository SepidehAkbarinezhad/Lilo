package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.*

import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import io.github.faridsolgi.date_picker.view.PersianDatePicker
import io.github.faridsolgi.date_picker.view.PersianDatePickerDefaults
import io.github.faridsolgi.date_picker.view.rememberPersianDatePickerState
import io.github.faridsolgi.persiandatetime.extensions.toEpochMilliseconds
import io.github.faridsolgi.persiandatetime.extensions.toPersianDateTime
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun ReminderCalendar(date: LocalDate, accent: Color, onDateChange: (LocalDate) -> Unit) {
    if (LocalLayoutDirection.current != LayoutDirection.Rtl) {
        GregorianReminderCalendar(date, accent, onDateChange)
        return
    }
    val state = rememberPersianDatePickerState(initialSelectedDate = LocalDateTime(date, LocalTime(12, 0)).toPersianDateTime())
    LaunchedEffect(state.selectedDate) {
        state.selectedDate?.let {
            onDateChange(Instant.fromEpochMilliseconds(it.toEpochMilliseconds()).toLocalDateTime(TimeZone.currentSystemDefault()).date)
        }
    }
    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(primary = accent, onPrimary = Color.Black)) {
        PersianDatePicker(state = state, title = {}, showModeToggle = false,
            colors = PersianDatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.background))
    }
}
