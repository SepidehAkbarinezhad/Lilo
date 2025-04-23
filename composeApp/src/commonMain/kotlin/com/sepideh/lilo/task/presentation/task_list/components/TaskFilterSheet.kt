package com.sepideh.lilo.task.presentation.task_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.data.ScreenSize
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppBottomSheet
import com.sepideh.lilo.core.presentation.components.AppRowButtons
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.task.presentation.model.TaskFilterOption
import com.sepideh.lilo.task.presentation.model.TaskStatus
import com.sepideh.lilo.task.presentation.task_list.TaskListEvent
import com.sepideh.lilo.task.presentation.task_list.TaskListState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.apply_label
import lilo.composeapp.generated.resources.filter_label
import lilo.composeapp.generated.resources.priority_filter_label
import lilo.composeapp.generated.resources.reset_label
import lilo.composeapp.generated.resources.status_filter_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskFilterSheet(
    state: TaskListState,
    onEvent: (BaseEvent) -> Unit,
    modifier: Modifier = Modifier,
) {

    val height = (.5 * ScreenSize.heightDp).dp

    AppBottomSheet(
        visible = state.isFilterSheetOpen,
        height = height,
        modifier = modifier.fillMaxWidth()
    ) {

        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                FilterHeader(onEvent)
                StatusFilterContainer(filterOption = state.tempFilterOption, onStatusClicked = {
                    onEvent(TaskListEvent.OnStatusFilterChanged(it))
                })
                PriorityFilterContainer(
                    filterOption = state.tempFilterOption,
                    onPriorityClicked = { selectedPriority ->
                        onEvent(TaskListEvent.OnPriorityFilterChanged(selectedPriority))
                    })


            }
            AppRowButtons(
                modifier = Modifier.align(Alignment.BottomCenter),
                firstButtonTitle = Res.string.apply_label,
                onFirstButtonClick = {
                    onEvent(TaskListEvent.OnApplyFilter)
                },
                secondButtonTitle = Res.string.reset_label,
                onSecondButtonClick = { onEvent(TaskListEvent.OnResetFilter) })
        }
    }
}

@Composable
fun FilterHeader(onEvent: (BaseEvent) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        AppText(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(Res.string.filter_label),
            textType = TextType.Title,
            color = MaterialTheme.colorScheme.tertiary
        )
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = { onEvent(TaskListEvent.OnFilterIcon) }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "close filter",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement =  Arrangement.spacedBy(16.dp)
    ) {
        TaskStatus.entries.forEach { status ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(status!=TaskStatus.ALL){
                    AppText(text = status.label)
                    Checkbox(
                        checked = status.label == filterOption.taskStatus?.label,
                        onCheckedChange = { onStatusClicked(status) }
                    )
                }
            }
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Priority.priorities.forEach { priority ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppText(text = priority.title)
                Checkbox(
                    checked = priority in filterOption.priorityList,
                    onCheckedChange = { onPriorityClicked(priority) }
                )
            }

        }
    }

}