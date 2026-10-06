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
        DatePicker(state, showModeToggle = false, colors = DatePickerDefaults.colors(selectedDayContainerColor = accent, selectedDayContentColor = com.sepideh.lilo.ui.theme.LiloExtendedTheme.colors.onAccent, todayDateBorderColor = accent))
    }
}
