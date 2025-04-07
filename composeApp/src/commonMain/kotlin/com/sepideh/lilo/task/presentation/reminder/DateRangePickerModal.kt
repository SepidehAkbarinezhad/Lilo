package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.runtime.Composable

@Composable
expect fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
)