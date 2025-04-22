package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.runtime.Composable
import com.sepideh.lilo.task.presentation.task_detail.ReminderModel

@Composable
expect fun TimePickerContainer(
    reminderModel: ReminderModel,
    onConfirm: (Pair<Int?,Int?>) -> Unit,
    onDismiss: () -> Unit,
)