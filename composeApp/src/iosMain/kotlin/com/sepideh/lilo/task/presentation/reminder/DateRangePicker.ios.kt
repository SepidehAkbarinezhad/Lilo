package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.sepideh.lilo.task.presentation.task_detail.ReminderModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun
        DateRangePickerModal(
    reminderModel: ReminderModel,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePicker(state = datePickerState)

    val selectedDateMillis = datePickerState.selectedDateMillis
    if (selectedDateMillis != null) {
        onDateRangeSelected(Pair(selectedDateMillis, second = null))
    }
}