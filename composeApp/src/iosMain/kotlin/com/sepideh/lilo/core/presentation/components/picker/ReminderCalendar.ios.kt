package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import io.github.faridsolgi.date_picker.view.PersianDatePicker
import io.github.faridsolgi.date_picker.view.PersianDatePickerDefaults
import io.github.faridsolgi.date_picker.view.rememberPersianDatePickerState
import io.github.faridsolgi.persiandatetime.extensions.toEpochMilliseconds
import io.github.faridsolgi.persiandatetime.extensions.toPersianDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun ReminderCalendar(
    date: LocalDate,
    accent: Color,
    onDateChange: (LocalDate) -> Unit,
) {
    val languageProvider: LanguageProvider = koinInject()

    // English → Compose Multiplatform Material 3
    if (languageProvider.currentLanguage != AppLanguage.FA) {
        GregorianReminderCalendar(
            date = date,
            accent = accent,
            onDateChange = onDateChange,
        )
        return
    }

    // Persian → FaridSolgi Persian/Jalali picker
    val state = rememberPersianDatePickerState(
        initialSelectedDate = LocalDateTime(
            date,
            LocalTime(12, 0)
        ).toPersianDateTime()
    )

    LaunchedEffect(state.selectedDate) {
        state.selectedDate?.let { selected ->
            onDateChange(
                Instant
                    .fromEpochMilliseconds(selected.toEpochMilliseconds())
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date
            )
        }
    }

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = accent,
            onPrimary = com.sepideh.lilo.ui.theme.LiloExtendedTheme.colors.onAccent,
        )
    ) {
        PersianDatePicker(
            state = state,
            title = {},
            showModeToggle = false,
            colors = PersianDatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background
            ),
        )
    }
}