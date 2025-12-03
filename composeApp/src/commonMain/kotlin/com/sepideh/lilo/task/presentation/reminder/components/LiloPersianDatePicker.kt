package com.sepideh.lilo.task.presentation.reminder.components

import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.BaseAction

@Composable
expect fun LiloPersianDatePicker(
    onAction: (BaseAction) -> Unit
)