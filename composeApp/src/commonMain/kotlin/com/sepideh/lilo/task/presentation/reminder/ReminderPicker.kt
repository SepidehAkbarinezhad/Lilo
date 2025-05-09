package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.presentation.reminder.components.ReminderTimePicker
import com.sepideh.lilo.task.presentation.task_detail.ReminderModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailEvent
import com.sepideh.lilo.utils.getCurrentDate
import com.sepideh.lilo.utils.getCurrentTime
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.apply_label
import lilo.composeapp.generated.resources.cancel_button

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderPicker(
    reminderModel: ReminderModel,
    onEvent: (BaseEvent) -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = reminderModel.startDay ?: getCurrentDate(),
    )
    val selectedDateMillis = datePickerState.selectedDateMillis

    val initialHour = reminderModel.hour ?: getCurrentTime().first
    val initialMinute = reminderModel.minute ?: getCurrentTime().second
   var selectedHour = initialHour
   var selectedMin = initialMinute


    Box(modifier = Modifier.fillMaxSize()){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.9f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier
                    .fillMaxWidth(),
                colors = DatePickerDefaults.colors(containerColor = Color.White),
                title = {
                    AppText(
                        modifier = Modifier.padding(4.dp),
                        text = "set reminder date ",
                        textType = TextType.SubTitle,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            )
            ReminderTimePicker(
                initialHour = initialHour,
                initialMinute = initialMinute,
                onSelectedHour = {selectedHour=it},
                onSelectedMinute ={selectedMin=it},
            )

        }

        AppRowButtons(
            modifier = Modifier.align(Alignment.BottomCenter).padding(18.dp),
            firstButtonTitle = Res.string.apply_label,
            onFirstButtonClick = {
                onEvent(
                    TaskDetailEvent.OnSelectReminderConfirm(
                        reminderModel = ReminderModel(
                            hour = selectedHour,
                            minute = selectedMin,
                            startDay = selectedDateMillis
                        )
                    )
                )
            },
            secondButtonTitle = Res.string.cancel_button,
            onSecondButtonClick = { onEvent(TaskDetailEvent.OnDismissReminderDialogButton)})

    }

}