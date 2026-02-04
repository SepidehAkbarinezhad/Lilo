@file:OptIn(ExperimentalMaterial3Api::class)

package com.sepideh.lilo.task.presentation.reminder.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import io.github.faridsolgi.date_picker.view.PersianDatePicker
import io.github.faridsolgi.date_picker.view.PersianDatePickerDefaults
import io.github.faridsolgi.date_picker.view.rememberPersianDatePickerState
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.extensions.toEpochMilliseconds
import io.github.faridsolgi.persiandatetime.extensions.toPersianDateTime
import io.github.faridsolgi.share.PersianDatePickerDialog
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
@Composable
actual fun LiloPersianDatePicker(selectedDay : Long? , onAction: (BaseAction) -> Unit) {
    val palette = LocalLiloColorsPalette.current

    val nowLocal = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val persianDate : PersianDateTime? = selectedDay?.let { PersianDateTime.parse(selectedDay) }
    val state = rememberPersianDatePickerState(initialSelectedDate = persianDate ?: nowLocal.toPersianDateTime())

    PersianDatePickerDialog(
        onDismissRequest = { },
        confirmButton = {
            ConfirmBtn(
                selectedDate = state.selectedDate?.toEpochMilliseconds(),
                onAction = onAction
            )
        },
        dismissButton = {
            CancelBtn(onAction = onAction)
        },
        colors =  PersianDatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        PersianDatePicker(state = state, title = { ReminderTitle(color = palette.primaryTitle) },
            colors =  PersianDatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),showModeToggle=false)
    }

}