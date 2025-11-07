package com.sepideh.lilo.task.presentation.reminder

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.core.utils.getCurrentDate
import com.sepideh.lilo.core.utils.getCurrentTime
import com.sepideh.lilo.core.utils.isPersianLanguage
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailAction
import com.sepideh.lilo.ui.theme.LiloColors
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import io.github.faridsolgi.date_picker.view.PersianDatePicker
import io.github.faridsolgi.date_picker.view.PersianDatePickerDefaults
import io.github.faridsolgi.date_picker.view.rememberPersianDatePickerState
import io.github.faridsolgi.persiandatetime.domain.PersianDateTime
import io.github.faridsolgi.persiandatetime.extensions.toEpochMilliseconds
import io.github.faridsolgi.persiandatetime.extensions.toPersianDateTime
import io.github.faridsolgi.share.PersianDatePickerDialog
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.apply_label
import lilo.composeapp.generated.resources.cancel_button
import lilo.composeapp.generated.resources.reminder_date_title
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun ReminderPicker(
    openDialog: Boolean,
    reminderModel: ReminderModel,
    onAction: (BaseAction) -> Unit,
) {

    val palette = LocalLiloColorsPalette.current

    val dateRangePickerState = rememberDateRangePickerState()

    val initialHour = reminderModel.hour ?: getCurrentTime().first
    val initialMinute = reminderModel.minute ?: getCurrentTime().second
    var selectedHour = initialHour
    var selectedMin = initialMinute


    when (isPersianLanguage()) {
        false -> {
                DefaultDatePicker(reminderModel = reminderModel, onAction = onAction)
        }
        true -> {
            PersianDatePicker(palette = palette, onAction = onAction)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun PersianDatePicker(palette: LiloColors, onAction: (BaseAction) -> Unit) {

    val nowLocal = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val state = rememberPersianDatePickerState(initialSelectedDate = nowLocal.toPersianDateTime())

    PersianDatePickerDialog(
        onDismissRequest = { },
        confirmButton = {
            ConfirmBtn(
                selectedDate = state.selectedDate?.toEpochMilliseconds(),
                onAction = onAction
            )
        },
        dismissButton = {
            CancelBtn(onAction = onAction)
        },
        colors =  PersianDatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        )
    ) {
        PersianDatePicker(state = state, title = { ReminderTitle(color = palette.primaryTitle) },
            colors =  PersianDatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),showModeToggle=false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultDatePicker(reminderModel: ReminderModel, onAction: (BaseAction) -> Unit) {
    val palette = LocalLiloColorsPalette.current
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = reminderModel.startDay ?: getCurrentDate(),
    )
    DatePickerDialog(
        onDismissRequest = {},
        confirmButton = {
            ConfirmBtn(
                onAction = onAction,
                selectedDate = datePickerState.selectedDateMillis
            )
        },
        dismissButton = {
            CancelBtn(onAction = onAction)
        },
        colors =  DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            selectedDayContentColor = Color.White,
            selectedDayContainerColor = palette.primaryTitle,
            headlineContentColor = MaterialTheme.colorScheme.onSurface,
        )
    ) {
        DatePicker(
            state = datePickerState,
            modifier = Modifier
                .fillMaxWidth().statusBarsPadding(),
            title = {
                ReminderTitle(color = palette.primaryTitle)
            },
            colors =  DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ),
            showModeToggle = false // Hides the pen/calendar icon
        )
    }

}


@Composable
fun ReminderTitle(color: Color) {
    AppText(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
        text = Res.string.reminder_date_title,
        textType = TextType.SubTitle,
        color = color
    )
}

@Composable
fun ConfirmBtn(selectedDate: Long?, onAction: (BaseAction) -> Unit) {
    AppText(
        modifier = Modifier.padding(8.dp).clickable {
            onAction(
                TaskDetailAction.OnSelectReminderConfirm(
                    reminderModel = ReminderModel(
                        startDay = selectedDate
                    )
                )
            )
        },
        text = Res.string.apply_label,
        textType = TextType.SubTitle,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun CancelBtn(onAction: (BaseAction) -> Unit) {
    AppText(
        modifier = Modifier.padding(8.dp).clickable {
            onAction(TaskDetailAction.OnDismissReminderDialogButton)
        },
        text = Res.string.cancel_button,
        textType = TextType.SubTitle,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
    )
}

