package com.sepideh.lilo.task.presentation.task_list.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.data.ScreenSize
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppBottomSheet
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.task.presentation.model.TaskFilterOption
import com.sepideh.lilo.task.presentation.model.TaskStatus
import com.sepideh.lilo.task.presentation.task_list.TaskListEvent
import com.sepideh.lilo.task.presentation.task_list.TaskListState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.filter_label
import lilo.composeapp.generated.resources.priority_filter_label
import lilo.composeapp.generated.resources.status_filter_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskFilterSheet(
    state: TaskListState,
    onEvent: (BaseEvent) -> Unit,
    modifier: Modifier = Modifier,
) {

    val height = (.5 * ScreenSize.heightDp).dp
    var filterOption by remember { mutableStateOf(state.taskFilterOption) }

    AppBottomSheet(
        visible = state.isFilterSheetOpen,
        height = height,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AppText(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(Res.string.filter_label),
                textType = TextType.Title,
                color = Color.White
            )
            IconButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { onEvent(TaskListEvent.OnFilterIcon) }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "close filter",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        StatusFilterContainer(filterOption = filterOption, onStatusClicked = {
            filterOption =
                TaskFilterOption(taskStatus = it, priority = filterOption.priority)
        })
        PriorityFilterContainer(filterOption = filterOption, onPriorityClicked = {
            filterOption =
                TaskFilterOption(taskStatus = filterOption.taskStatus, priority = it )
        })
    }
}

@Composable
fun StatusFilterContainer(
    filterOption: TaskFilterOption,
    onStatusClicked: (TaskStatus) -> Unit
) {
    AppText(text = stringResource(Res.string.status_filter_label), textType = TextType.SubTitle)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskStatus.entries.forEach { status ->
            AppText(text = status.label)
            RadioButton(
                selected = status.label == filterOption.taskStatus.label,
                onClick = { onStatusClicked(status) })
        }
    }
}

@Composable
fun PriorityFilterContainer(
    filterOption: TaskFilterOption,
    onPriorityClicked: (Priority) -> Unit
) {
    AppText(text = stringResource(Res.string.priority_filter_label), textType = TextType.SubTitle)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Priority.priorities.forEach { priority ->
            AppText(text = priority.title)
            RadioButton(
                selected = priority == filterOption.priority,
                onClick = { onPriorityClicked(priority) })
        }
    }
}