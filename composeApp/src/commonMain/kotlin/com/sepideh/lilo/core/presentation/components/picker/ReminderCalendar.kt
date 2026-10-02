package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.*
import kotlin.time.Instant

@Composable
expect fun ReminderCalendar(date: LocalDate, accent: Color, onDateChange: (LocalDate) -> Unit)

/** Material date picker values represent UTC midnight, not local instants. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GregorianReminderCalendar(date: LocalDate, accent: Color, onDateChange: (LocalDate) -> Unit) {
    val state = rememberDatePickerState(initialSelectedDateMillis = date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds())
    LaunchedEffect(state.selectedDateMillis) {
        state.selectedDateMillis?.let { onDateChange(Instant.fromEpochMilliseconds(it).toLocalDateTime(TimeZone.UTC).date) }
    }
    DatePicker(state, title = null, headline = null, showModeToggle = false,
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.background,
            selectedDayContainerColor = accent, selectedDayContentColor = Color.Black,
            todayDateBorderColor = accent, todayContentColor = MaterialTheme.colorScheme.onSurface))
}
