package com.sepideh.lilo.task.presentation.list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.SheetHeader
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.task.presentation.list.*
import com.sepideh.lilo.task.presentation.model.*
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TaskFilterSheet(state: TaskListState, onAction: (BaseAction) -> Unit, modifier: Modifier = Modifier) {
    if (!state.isFilterSheetOpen) return
    val accent = LiloExtendedTheme.colors.taskColor
    ModalBottomSheet(onDismissRequest = { onAction(TaskListAction.OnCloseFilterIcon) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.extraLarge, containerColor = MaterialTheme.colorScheme.background) {
        Column(modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = LiloSpacing.Screen).padding(bottom = LiloSpacing.Section),
            verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
            SheetHeader(Res.string.filter_label, Res.string.apply_label, accent, onConfirm = { onAction(TaskListAction.OnApplyFilter) })
            AppText(text = Res.string.status_filter_label, textType = TextType.FieldLabel)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Small), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Tiny)) {
                listOf(Enums.DONE, Enums.UNDONE).forEach { status ->
                    LiloSelectionChip(stringResource(status.label), status in state.tempFilterOption.taskStatus, accent, { onAction(TaskListAction.OnStatusFilterChanged(status)) })
                }
            }
            AppText(text = Res.string.priority_filter_label, textType = TextType.FieldLabel)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Small), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Tiny)) {
                Priority.priorities.forEach { priority ->
                    LiloSelectionChip(stringResource(priority.title), priority in state.tempFilterOption.priorityList, accent, { onAction(TaskListAction.OnPriorityFilterChanged(priority)) })
                }
            }
            TextButton(onClick = { onAction(TaskListAction.OnResetFilter) }, colors = ButtonDefaults.textButtonColors(contentColor = LiloExtendedTheme.colors.textSecondary)) {
                AppText(text = Res.string.reset_label, textType = TextType.Action)
            }
        }
    }
}
