package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sepideh.lilo.task.presentation.task_detail.ReminderModel

@Composable
actual fun TimePickerContainer(
    onConfirm: (Pair<Int?,Int?>) -> Unit,
    onDismiss: () -> Unit
) {
}