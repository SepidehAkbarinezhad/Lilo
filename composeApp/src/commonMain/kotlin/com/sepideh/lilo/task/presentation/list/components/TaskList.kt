package com.sepideh.lilo.task.presentation.list.components

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
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.list.TaskListScreen
import com.sepideh.lilo.task.presentation.list.TaskListState
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.no_group_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskList(
    tasks: List<Task>,
    clickable: Boolean,
    onAction: (BaseAction) -> Unit,
    modifier: Modifier = Modifier,
    groupLabels: Map<Long, String> = emptyMap(),
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 96.dp)
    ) {
        items(items = tasks, key = { it.id ?: 0 }) { task ->
            TaskListItem(
                modifier = Modifier.fillMaxWidth().clickable {
                    if (clickable) {
                        onAction(
                            BaseAction.OnNavigateTo(
                                    (AppRoutes.Tasks.Detail(taskId = task.id))
                            )
                        )
                    }

                },
                clickable = clickable,
                task = task,
                groupLabel = groupLabels[task.category] ?: stringResource(Res.string.no_group_label),
                onAction = onAction
            )
        }
    }
}


@AppPreviews
@Composable
private fun TaskListPreview() {
    LiloPreviewWrapper {
        val task = Task(title = "Practice violin", description = "play violin", priority = 1)
        TaskList(
            tasks = listOf(task),
            clickable = true,
            onAction = {},
        )
    }
}
