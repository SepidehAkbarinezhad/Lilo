package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.runtime.Composable
import com.sepideh.lilo.task.presentation.task_detail.ReminderModel

@Composable
expect fun DateRangePickerModal(
    reminderModel: ReminderModel,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
)