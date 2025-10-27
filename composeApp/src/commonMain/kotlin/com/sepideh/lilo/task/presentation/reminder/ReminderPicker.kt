package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.utils.getCurrentDate
import com.sepideh.lilo.core.utils.getCurrentTime
import com.sepideh.lilo.task.presentation.task_detail.ReminderModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailAction
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.cancel_button
import lilo.composeapp.generated.resources.confirm_button
import lilo.composeapp.generated.resources.reminder_date_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderPicker(
    reminderModel: ReminderModel,
    onAction: (BaseAction) -> Unit,
) {
    val palette = LocalLiloColorsPalette.current
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = reminderModel.startDay ?: getCurrentDate(),
    )
    val dateRangePickerState = rememberDateRangePickerState()
    val selectedDateMillis = datePickerState.selectedDateMillis

    val initialHour = reminderModel.hour ?: getCurrentTime().first
    val initialMinute = reminderModel.minute ?: getCurrentTime().second
    var selectedHour = initialHour
    var selectedMin = initialMinute


    DatePickerDialog(
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
        onDismissRequest = {},
        confirmButton = {
            AppText(modifier = Modifier.clickable {
                onAction(
                    TaskDetailAction.OnSelectReminderConfirm(
                        reminderModel = ReminderModel(
                            startDay = datePickerState.selectedDateMillis
                        )
                    )
                )
            }, text = Res.string.confirm_button , color = MaterialTheme.colorScheme.secondary)
        },
        dismissButton = {
            AppText(modifier = Modifier.clickable {
                onAction(TaskDetailAction.OnDismissReminderDialogButton)
            }, text = Res.string.cancel_button)
        }
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier
                .fillMaxWidth().statusBarsPadding(),
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface,
                selectedDayContentColor = Color.White,
                selectedDayContainerColor = palette.primaryTitle,
                headlineContentColor = MaterialTheme.colorScheme.onSurface,
            ),
            title = {
                AppText(
                    modifier = Modifier.padding(16.dp),
                    text = Res.string.reminder_date_title,
                    textType = TextType.SubTitle,
                    color = palette.primaryTitle
                )
            },
            showModeToggle = false // Hides the pen/calendar icon
        )
    }
}

