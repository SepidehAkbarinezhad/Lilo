package com.sepideh.lilo.task.presentation.list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.SheetHeader
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.task.presentation.list.*
import com.sepideh.lilo.task.presentation.model.*
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFilterSheet(state: TaskListState, onAction: (BaseAction) -> Unit, modifier: Modifier = Modifier) {
    if (!state.isFilterSheetOpen) return
    ModalBottomSheet(onDismissRequest = { onAction(TaskListAction.OnCloseFilterIcon) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.extraLarge, containerColor = MaterialTheme.colorScheme.background) {
        TaskFilterContent(state, onAction, modifier)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TaskFilterContent(state: TaskListState, onAction: (BaseAction) -> Unit, modifier: Modifier = Modifier) {
    val colors = LiloExtendedTheme.colors
    Column(modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        .padding(horizontal = 28.dp).padding(bottom = LiloSpacing.Section),
        verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
        SheetHeader(Res.string.filter_label, Res.string.apply_label, colors.taskColor,
            onConfirm = { onAction(TaskListAction.OnApplyFilter) },
            onReset = { onAction(TaskListAction.OnResetFilter) }, resetActionColor = colors.taskAction)
        AppText(text = Res.string.status_filter_label, textType = TextType.FieldLabel)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Item),
            verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
            listOf(Enums.DONE, Enums.UNDONE).forEach { status ->
                TaskFilterChoice(stringResource(status.label), status in state.tempFilterOption.taskStatus,
                    { onAction(TaskListAction.OnStatusFilterChanged(status)) }, Modifier.widthIn(min = 104.dp))
            }
        }
        HorizontalDivider(Modifier.padding(top = LiloSpacing.Small), color = MaterialTheme.colorScheme.outlineVariant)
        AppText(text = Res.string.priority_filter_label, textType = TextType.FieldLabel)
        FlowRow(Modifier.fillMaxWidth(), maxItemsInEachRow = 3,
            horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Item),
            verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
            Priority.priorities.forEach { priority ->
                TaskFilterChoice(stringResource(priority.title), priority in state.tempFilterOption.priorityList,
                    { onAction(TaskListAction.OnPriorityFilterChanged(priority)) }, Modifier.weight(1f).widthIn(min = 80.dp))
            }
        }
    }
}

@Composable
private fun TaskFilterChoice(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LiloExtendedTheme.colors
    Surface(modifier = modifier.heightIn(min = LiloSize.TouchTarget)
        .toggleable(value = selected, role = Role.Checkbox, onValueChange = { onClick() }),
        shape = MaterialTheme.shapes.small,
        color = if (selected) colors.taskColor.copy(alpha = .3f) else MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, if (selected) colors.taskAction else MaterialTheme.colorScheme.outlineVariant)) {
        Box(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
            AppText(text = label, textType = TextType.Body, textAlign = TextAlign.Center,
                color = if (selected) colors.title else colors.textSecondary)
        }
    }
}

@AppPreviews
@Composable
private fun TaskFiltersSelectedPreview() {
    LiloPreviewWrapper {
        TaskFilterContent(TaskListState(tempFilterOption = TaskFilterOption(
            taskStatus = listOf(Enums.UNDONE), priorityList = listOf(Priority.getById(0)))), {})
    }
}

@AppPreviews
@Composable
private fun TaskFiltersEmptyPreview() {
    LiloPreviewWrapper { TaskFilterContent(TaskListState(), {}) }
}
