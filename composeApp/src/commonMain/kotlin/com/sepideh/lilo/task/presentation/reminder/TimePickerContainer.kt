package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.runtime.Composable

@Composable
expect fun TimePickerContainer(
    onConfirm: (Pair<Int?,Int?>) -> Unit,
    onDismiss: () -> Unit,
)