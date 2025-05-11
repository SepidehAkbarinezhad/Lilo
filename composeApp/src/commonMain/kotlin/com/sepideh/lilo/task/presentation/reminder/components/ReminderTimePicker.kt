package com.sepideh.lilo.task.presentation.reminder.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText

@Composable
fun ColumnScope.ReminderTimePicker(
    initialHour: Int,
    initialMinute: Int,
    onSelectedHour: (Int) -> Unit,
    onSelectedMinute: (Int) -> Unit,
) {
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
            .collect { index ->
                onSelectedHour(index)
            }
    }

    LaunchedEffect(minuteListState) {
        snapshotFlow { minuteListState.firstVisibleItemIndex }
            .collect { index ->
                onSelectedMinute(index)
            }
    }

    AppText(
        modifier = Modifier.padding(4.dp),
        text = "set reminder time ",
        textType = TextType.SubTitle,
        color = MaterialTheme.colorScheme.primary
    )
    // Custom Time Picker
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
            Text("Hour", style = MaterialTheme.typography.bodyMedium)
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
                                text = hour.toString().padStart(2, '0')
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
            Text("Minute", style = MaterialTheme.typography.bodyMedium)
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
                                text =minute.toString().padStart(2, '0')
                            )
                        }
                    }
                }
            }

        }
    }
}