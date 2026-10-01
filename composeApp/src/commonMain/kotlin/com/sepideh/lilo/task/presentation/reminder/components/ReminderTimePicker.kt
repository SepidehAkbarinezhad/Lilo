package com.sepideh.lilo.task.presentation.reminder.components

import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.picker.FeatureTimePicker
import com.sepideh.lilo.core.utils.getCurrentTime
import com.sepideh.lilo.task.presentation.detail.TaskDetailAction
import com.sepideh.lilo.task.presentation.reminder.ReminderModel
import com.sepideh.lilo.ui.theme.LiloExtendedTheme

@Composable
fun ReminderTimePicker(reminderModel: ReminderModel, onAction: (BaseAction) -> Unit) {
    val now = getCurrentTime()
    FeatureTimePicker(hour = reminderModel.reminderHour ?: now.first, minute = reminderModel.reminderMinute ?: now.second,
        accent = LiloExtendedTheme.colors.taskColor,
        onConfirm = { hour, minute -> onAction(TaskDetailAction.OnReminderTimeConfirm(ReminderModel(reminderHour = hour, reminderMinute = minute))) },
        onDismiss = { onAction(TaskDetailAction.OnDismissTimePickerButton) })
}
