package com.sepideh.lilo.task.presentation.reminder.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppDialog
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.core.utils.getCurrentTime
import com.sepideh.lilo.task.presentation.reminder.ReminderModel
import com.sepideh.lilo.task.presentation.task_detail.TaskDetailAction
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.cancel_button
import lilo.composeapp.generated.resources.hour_label
import lilo.composeapp.generated.resources.minute_label
import lilo.composeapp.generated.resources.ok_label
import lilo.composeapp.generated.resources.reminder_time_title
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReminderTimePicker(
    reminderModel: ReminderModel,
    onAction: (BaseAction) -> Unit,
) {
    val initialHour = reminderModel.hour ?: getCurrentTime().first
    val initialMinute = reminderModel.minute ?: getCurrentTime().second
    var selectedHour = initialHour
    var selectedMin = initialMinute
    val palette = LocalLiloColorsPalette.current
    val hourListState = rememberLazyListState()
    val hourSnapFlingBehavior = rememberSnapFlingBehavior(lazyListState = hourListState)

    val minuteListState = rememberLazyListState()
    val minuteSnapFlingBehavior = rememberSnapFlingBehavior(lazyListState = minuteListState)


    LaunchedEffect(Unit) {
        hourListState.scrollToItem(initialHour)
        minuteListState.scrollToItem(initialMinute)
    }

    LaunchedEffect(hourListState) {
        snapshotFlow { hourListState.firstVisibleItemIndex }
            .collect { hour -> { selectedHour = hour } }
    }

    LaunchedEffect(minuteListState) {


        snapshotFlow { minuteListState.firstVisibleItemIndex }
            .collect { minute ->
                selectedMin = minute
            }
    }
    AppDialog(dialogModel = DialogModel(content = {
        Column {
            AppText(
                modifier = Modifier.padding(4.dp),
                text = Res.string.reminder_time_title,
                textType = TextType.SubTitle,
                color = palette.primaryTitle
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // Hour Picker
                    Column(
                        modifier = Modifier.padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppText(text = Res.string.hour_label, textType = TextType.Body)
                        Card(
                            modifier = Modifier.size(50.dp),
                            border = BorderStroke(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                state = hourListState,
                                flingBehavior = hourSnapFlingBehavior
                            ) {
                                items(24) { hour ->
                                    Box(
                                        modifier = Modifier.size(50.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppText(
                                            text = hour.toString().padStart(2, '0'),
                                            color = Color.Black
                                        )
                                    }

                                }
                            }
                        }
                    }

                    // Minute Picker
                    Column(
                        modifier = Modifier.padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppText(text = Res.string.minute_label, textType = TextType.Body)
                        Card(
                            modifier = Modifier.size(50.dp),
                            border = BorderStroke(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                state = minuteListState,
                                flingBehavior = minuteSnapFlingBehavior
                            ) {
                                items(60) { minute ->
                                    Box(
                                        modifier = Modifier.size(50.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppText(
                                            text = minute.toString().padStart(2, '0'),
                                            color = Color.Black
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
            Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
            TimePickerButtons(onConfirmed = {
                onAction(TaskDetailAction.OnReminderTimeConfirm(
                    reminderModel = ReminderModel(
                        hour = selectedHour,
                        minute = selectedMin
                    )
                )
                ) }, onCancel = { onAction(TaskDetailAction.OnDismissTimePickerButton) })
        }
    }, onDismissRequest = {}))
}

@Composable
fun TimePickerButtons(onConfirmed: () -> Unit, onCancel: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        val layoutDirection = LocalLayoutDirection.current
            AppText(
                modifier = Modifier.padding(8.dp).clickable { onCancel() },
                text = Res.string.cancel_button,
                textType = TextType.SubTitle,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f)
            )
            AppText(
                modifier = Modifier.padding(8.dp).clickable { onConfirmed() },
                text = Res.string.ok_label,
                textType = TextType.SubTitle,
                color = MaterialTheme.colorScheme.primary
            )

    }
}

@Preview
@Composable
fun ReminderTimePickerPrev() {
    ReminderTimePicker(
        reminderModel = ReminderModel(),
        onAction = {}
    )
}