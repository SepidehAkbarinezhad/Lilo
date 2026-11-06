package com.sepideh.lilo.task.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.core.presentation.components.LiloSelectionChip
import com.sepideh.lilo.task.presentation.TaskGroupUi
import com.sepideh.lilo.task.presentation.list.TaskListAction
import com.sepideh.lilo.task.presentation.list.TaskListState
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSpacing
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.group_all
import lilo.composeapp.generated.resources.no_group_label
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskGroupList(state: TaskListState, clickable: Boolean, onAction: (BaseAction) -> Unit) {
    val accent = LiloExtendedTheme.colors.taskColor
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Small),
    ) {
        item(key = "all") {
            LiloSelectionChip(
                label = stringResource(Res.string.group_all), selected = state.selectedGroup == null,
                accent = accent, enabled = clickable,
                onClick = { onAction(TaskListAction.OnGroupSelected(null)) },
            )
        }
        item(key = "no_group") {
            LiloSelectionChip(
                label = stringResource(Res.string.no_group_label), selected = state.selectedGroup == 0L,
                accent = accent, enabled = clickable,
                onClick = { onAction(TaskListAction.OnGroupSelected(0L)) },
            )
        }
        items(state.groups, key = { it.id }) { group ->
            LiloSelectionChip(
                label = group.title, selected = state.selectedGroup == group.id,
                accent = accent, enabled = clickable,
                onClick = { onAction(TaskListAction.OnGroupSelected(group.id)) },
            )
        }
    }
}

@AppPreviews
@Composable
private fun TaskGroupsPreview() {
    LiloPreviewWrapper {
        TaskGroupList(TaskListState(groups = listOf(TaskGroupUi(7, "Work"))), true, {})
    }
}

@AppPreviews
@Composable
private fun TaskGroupsWithoutGroupSelectedPreview() {
    LiloPreviewWrapper {
        TaskGroupList(TaskListState(selectedGroup = 0L, groups = listOf(TaskGroupUi(7, "Work"))), true, {})
    }
}

@AppPreviews
@Composable
private fun TaskGroupsEmptyPreview() {
    LiloPreviewWrapper { TaskGroupList(TaskListState(selectedGroup = 0L), true, {}) }
}
