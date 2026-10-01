package com.sepideh.lilo.task.presentation.list.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.SheetHeader
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.presentation.list.TaskListAction
import com.sepideh.lilo.task.presentation.list.TaskListState
import com.sepideh.lilo.task.presentation.model.Enums
import com.sepideh.lilo.task.presentation.model.Priority
import com.sepideh.lilo.task.presentation.model.SortOrder
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskFilterSheet(state: TaskListState, onAction: (BaseAction) -> Unit, modifier: Modifier = Modifier) {
    if (!state.isFilterSheetOpen) return
    val accent = LiloExtendedTheme.colors.taskColor
    ModalBottomSheet(onDismissRequest = { onAction(TaskListAction.OnCloseFilterIcon) }, containerColor = MaterialTheme.colorScheme.background) {
        Column(modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SheetHeader(Res.string.filter_label, Res.string.apply_label, accent, onConfirm = { onAction(TaskListAction.OnApplyFilter) })
            AppText(text = Res.string.status_filter_label, textType = TextType.SectionTitle)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(Enums.DONE, Enums.UNDONE).forEach { status ->
                    FilterChip(selected = status in state.tempFilterOption.taskStatus, onClick = { onAction(TaskListAction.OnStatusFilterChanged(status)) }, label = { AppText(text = status.label) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accent.copy(alpha = .16f)))
                }
            }
            AppText(text = Res.string.priority_filter_label, textType = TextType.SectionTitle)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.priorities.forEach { priority ->
                    FilterChip(selected = priority in state.tempFilterOption.priorityList, onClick = { onAction(TaskListAction.OnPriorityFilterChanged(priority)) }, label = { AppText(text = priority.title) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accent.copy(alpha = .16f)))
                }
            }
            TextButton(onClick = { onAction(TaskListAction.OnResetFilter) }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)) { AppText(text = Res.string.reset_label, textType = TextType.Action) }
        }
    }
}
