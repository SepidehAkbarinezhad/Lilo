package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.DialogModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TimePickerContainer(
    modifier: Modifier,
    onConfirm: (Pair<Int?, Int?>) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    AppDialog(
        dialogModel = DialogModel(content = {
            Column(modifier = Modifier.padding(24.dp)) {
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