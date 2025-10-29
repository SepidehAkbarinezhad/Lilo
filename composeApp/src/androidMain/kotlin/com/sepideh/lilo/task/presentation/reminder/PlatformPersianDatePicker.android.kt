package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.gmail.hamedvakhide.compose_jalali_datepicker.JalaliDatePickerDialog
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.utils.jalaliToEpochMillis
import com.sepideh.lilo.core.utils.millisToJalali
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailAction
import ir.huri.jcal.JalaliCalendar

@Composable
actual fun PlatformPersianDatePicker(
    openDialog: Boolean,
    reminderModel: ReminderModel,
    onAction: (BaseAction) -> Unit
) {
    val open = remember { mutableStateOf(openDialog) }

    JalaliDatePickerDialog(
        openDialog = open,
        initialDate = reminderModel.startDay?.let { millisToJalali(it) } ?: JalaliCalendar(),
        onSelectDay = {},
        onConfirm = {
            onAction(
                TaskDetailAction.OnSelectReminderConfirm(
                    reminderModel = ReminderModel(
                        startDay = jalaliToEpochMillis(jalaliDate = it)
                    )
                )
            )
            onAction(TaskDetailAction.OnDismissReminderDialogButton)
        },
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        selectedIconColor = MaterialTheme.colorScheme.primary,
        textColorHighlight = MaterialTheme.colorScheme.secondary,
        confirmBtnColor = MaterialTheme.colorScheme.primary,
        cancelBtnColor = MaterialTheme.colorScheme.onSurface,
        textColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Preview
@Composable
private fun PlatformPersianDatePickerPrev() {
    val open by remember { mutableStateOf(true) }
    PlatformPersianDatePicker(openDialog = open, reminderModel = ReminderModel(), onAction = {})
}
