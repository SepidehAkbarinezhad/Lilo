package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.*

@Composable
actual fun ReminderCalendar(date: LocalDate, accent: Color, onDateChange: (LocalDate) -> Unit) {
    GregorianReminderCalendar(date, accent, onDateChange)
}
