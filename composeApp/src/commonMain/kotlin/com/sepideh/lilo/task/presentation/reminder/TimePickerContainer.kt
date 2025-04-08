package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun TimePickerContainer(
    modifier: Modifier=Modifier,
    onConfirm: (Pair<Int?,Int?>) -> Unit,
    onDismiss: () -> Unit,
)