package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.BaseAction

@Composable
expect fun PlatformPersianDatePicker(
    openDialog: Boolean,
    reminderModel: ReminderModel,
    onAction: (BaseAction) -> Unit
)
