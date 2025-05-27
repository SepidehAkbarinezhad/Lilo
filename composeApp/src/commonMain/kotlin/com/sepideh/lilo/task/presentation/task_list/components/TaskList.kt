package com.sepideh.lilo.task.presentation.task_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.task.domain.model.Task

@Composable
fun TaskList(
    tasks: List<Task>,
    clickable: Boolean,
    onEvent: (BaseEvent) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 12.dp, bottom = 54.dp)
    ) {
        items(items = tasks, key = { it.id ?: 0 }) { task ->
            println("cancleeee items ${task.id}")
            TaskListItem(
                modifier = Modifier.fillMaxWidth().clickable {
                    if (clickable) {
                        onEvent(
                            BaseEvent.OnNavigateTo(
                                AppDestinations.TaskDetail(
                                    (AppRoutes.TaskDetail(taskId = task.id))
                                )
                            )
                        )
                    }

                }.padding(horizontal = 12.dp),
                clickable = clickable,
                task = task,
                onEvent = onEvent
            )
        }
    }
}