package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Selection only; permissions and scheduling belong to the caller. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureDatePicker(selectedDateMillis: Long?, accent: Color, onConfirm: (Long) -> Unit, onDismiss: () -> Unit) {
    val state = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    DatePickerDialog(onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = { state.selectedDateMillis?.let(onConfirm) }, enabled = state.selectedDateMillis != null,
            colors = ButtonDefaults.textButtonColors(contentColor = accent)) { Text(stringResource(Res.string.confirm_action)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel_button)) } }) {
        DatePicker(state, showModeToggle = false, colors = DatePickerDefaults.colors(selectedDayContainerColor = accent, selectedDayContentColor = Color.Black, todayDateBorderColor = accent))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureTimePicker(hour: Int, minute: Int, accent: Color, onConfirm: (Int, Int) -> Unit, onDismiss: () -> Unit) {
    val state = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = true)
    AlertDialog(onDismissRequest = onDismiss,
        text = { TimeInput(state = state, colors = TimePickerDefaults.colors(timeSelectorSelectedContainerColor = accent.copy(alpha = .16f))) },
        confirmButton = { TextButton(onClick = { onConfirm(state.hour, state.minute) }, colors = ButtonDefaults.textButtonColors(contentColor = accent)) { Text(stringResource(Res.string.confirm_action)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel_button)) } })
}
