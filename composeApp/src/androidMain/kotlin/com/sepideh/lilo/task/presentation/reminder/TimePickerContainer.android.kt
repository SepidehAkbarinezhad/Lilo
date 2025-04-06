package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sepideh.lilo.task.presentation.task_detail.ReminderModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TimePickerContainer(
    modifier: Modifier,
    onConfirm: (ReminderModel) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    Dialog(onDismissRequest = onDismiss) {
        Column(modifier = modifier.background(Color.White).padding(24.dp)) {
            TimePicker(
                state = timePickerState,
            )
            Button(onClick = onDismiss) {
                Text("Dismiss picker")
            }
            Button(onClick = {onConfirm(ReminderModel(hour = timePickerState.hour, minute = timePickerState.minute))}) {
                Text("Confirm selection")
            }
        }
    }

}