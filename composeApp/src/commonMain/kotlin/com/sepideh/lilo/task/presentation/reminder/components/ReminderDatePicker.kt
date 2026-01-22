package com.sepideh.lilo.task.presentation.reminder.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.utils.PlatformType
import com.sepideh.lilo.core.utils.getCurrentDate
import com.sepideh.lilo.core.utils.getPlatformType
import com.sepideh.lilo.settings.domain.usecase.LanguageProvider
import com.sepideh.lilo.settings.presentation.model.AppLanguage
import com.sepideh.lilo.task.presentation.reminder.ReminderModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailAction
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.cancel_button
import lilo.composeapp.generated.resources.ok_label
import lilo.composeapp.generated.resources.reminder_date_title
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun ReminderDatePicker(
    reminderModel: ReminderModel,
    onAction: (BaseAction) -> Unit,
) {

    val languageProvider: LanguageProvider = koinInject()
    when (languageProvider.currentLanguage) {
        AppLanguage.FA -> {
            if (getPlatformType().name == PlatformType.ANDROID.name)
                LiloPersianDatePicker(onAction = onAction)
        }
        AppLanguage.EN -> DefaultDatePicker(reminderModel = reminderModel, onAction = onAction)
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
        colors = DatePickerDefaults.colors(
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
            colors = DatePickerDefaults.colors(
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
                TaskDetailAction.OnReminderDateConfirm(
                    reminderModel = ReminderModel(
                        startDay = selectedDate
                    )
                )
            )
        },
        text = Res.string.ok_label,
        textType = TextType.SubTitle,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun CancelBtn(onAction: (BaseAction) -> Unit) {
    AppText(
        modifier = Modifier.padding(8.dp).clickable {
            onAction(TaskDetailAction.OnDismissDatePickerButton)
        },
        text = Res.string.cancel_button,
        textType = TextType.SubTitle,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
    )
}


@Preview
@Composable
fun DatePickerPrev() {
    ReminderDatePicker(reminderModel = ReminderModel(), onAction = {})
}

