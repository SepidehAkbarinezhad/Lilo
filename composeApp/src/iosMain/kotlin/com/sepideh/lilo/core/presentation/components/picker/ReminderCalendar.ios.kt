package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.LocalDate

// Temporary Gregorian fallback for both languages until the Persian picker supports our iOS build.
@Composable
actual fun ReminderCalendar(
    date: LocalDate,
    accent: Color,
    onDateChange: (LocalDate) -> Unit,
) {
    GregorianReminderCalendar(date = date, accent = accent, onDateChange = onDateChange)
}
