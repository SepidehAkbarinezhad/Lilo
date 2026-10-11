package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sepideh.lilo.core.presentation.components.DialogActions
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Selection only; permissions and scheduling belong to the caller. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeatureDatePicker(selectedDateMillis: Long?, accent: Color, onConfirm: (Long) -> Unit, onDismiss: () -> Unit) {
    val state = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
    DatePickerDialog(onDismissRequest = onDismiss,
        confirmButton = {
            DialogActions(accent, stringResource(Res.string.confirm_action), stringResource(Res.string.cancel_button),
                onConfirm = { state.selectedDateMillis?.let(onConfirm) }, onCancel = onDismiss,
                confirmEnabled = state.selectedDateMillis != null)
        }) {
        DatePicker(state, showModeToggle = false, colors = DatePickerDefaults.colors(selectedDayContainerColor = accent, selectedDayContentColor = com.sepideh.lilo.ui.theme.LiloExtendedTheme.colors.onAccent, todayDateBorderColor = accent))
    }
}
