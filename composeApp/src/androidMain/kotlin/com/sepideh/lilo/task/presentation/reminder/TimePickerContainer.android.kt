package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.task.presentation.task_detail.ReminderModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TimePickerContainer(
    reminderModel: ReminderModel,
    onConfirm: (Pair<Int?, Int?>) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = reminderModel.hour ?: currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = reminderModel.minute ?: currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    AppDialog(
        dialogModel = DialogModel(content = {
            Column {
                TimePicker(
                    state = timePickerState,
                )
                Button(onClick = onDismiss) {
                    Text("Dismiss picker")
                }
                Button(onClick = {
                    onConfirm(
                        Pair(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    )
                }) {
                    Text("Confirm selection")
                }
            }
        }, onDismissRequest = onDismiss),
    )

}