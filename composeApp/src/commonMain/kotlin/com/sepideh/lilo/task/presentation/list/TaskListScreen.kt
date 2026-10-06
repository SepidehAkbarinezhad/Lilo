package com.sepideh.lilo.task.presentation.list

import com.sepideh.lilo.ui.theme.*

import com.sepideh.lilo.core.presentation.icons.LiloIcons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.BaseListScreen
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloSelectionChip
import com.sepideh.lilo.core.presentation.components.DeleteConfirmationDialog
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.task.domain.model.Task
import com.sepideh.lilo.task.presentation.detail.TaskDetailScreen
import com.sepideh.lilo.task.presentation.detail.TaskDetailState
import com.sepideh.lilo.task.presentation.list.components.TaskFilterSheet
import com.sepideh.lilo.task.presentation.list.components.TaskList
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun TaskListScreenRoot(viewModel: TaskListViewModel, onNavigateTo: (AppRoutes) -> Unit, onBack: () -> Boolean) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BaseRoot(viewModel = viewModel, navigateTo = onNavigateTo, onBack = onBack,
        bodyContainer = { TaskListScreen(state, state.isLoading, viewModel::onAction) },
        dialogContent = {
            if (state.isDeleteDialogOpen) DeleteConfirmationDialog(title =  stringResource(Res.string.delete_task_confirmation_title),accent = LiloExtendedTheme.colors.taskColor,
                onConfirm = { viewModel.onAction(TaskListAction.OnDeleteTaskConfirm) },
                onDismiss = { viewModel.onAction(TaskListAction.OnDismissDeleteDialog) })
        })
}

@Composable
fun TaskListScreen(state: TaskListState, isLoading: Boolean = false, onAction: (BaseAction) -> Unit) {
    val accent = LiloExtendedTheme.colors.taskColor
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    BaseListScreen(title = Res.string.tasks_list_title, accent = accent, referenceStyle = true,
        searchVisible = state.isSearchVisible, query = state.searchQuery, searchHint = Res.string.search_tasks_action,
        filtersActive = state.taskFilterOption.taskStatus.isNotEmpty() || state.taskFilterOption.priorityList.isNotEmpty(),
        onBack = { onAction(BaseAction.OnNavigateTo(null)); true },
        onSearchVisible = { onAction(TaskListAction.OnSearchToggle(it)) },
        onQueryChange = { onAction(TaskListAction.OnSearchQueryChange(it)) },
        onFilter = { onAction(TaskListAction.OnFilterIcon) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                focusManager.clearFocus()
                keyboard?.hide()
                onAction(TaskListAction.OnSearchToggle(false))
                onAction(BaseAction.OnNavigateTo(AppRoutes.Tasks.Detail(null)))
            }, modifier = Modifier.size(64.dp), shape = androidx.compose.foundation.shape.CircleShape, containerColor = accent, contentColor = MaterialTheme.colorScheme.onSurface) {
                Icon(LiloIcons.Add, stringResource(Res.string.add_task_label), Modifier.size(36.dp))
            }
        }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (state.hasError) Text(stringResource(Res.string.task_operation_error), Modifier.padding(20.dp), color = MaterialTheme.colorScheme.error)
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
                items(state.categories, key = { it.id }) { category ->
                    val id = category.id.takeIf { it != 0L }
                    LiloSelectionChip(label = category.title, selected = state.selectedCategory == id, accent = accent,
                        onClick = { onAction(TaskListAction.OnCategorySelected(id)) })
                }
            }
            if (isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = accent)
            if (state.tasksResult.isEmpty() && !isLoading) Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(shape = androidx.compose.foundation.shape.CircleShape, color = accent.copy(alpha = .08f)) {
                        Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) { Icon(LiloIcons.Empty, null, Modifier.size(36.dp), tint = LiloExtendedTheme.colors.textSupporting) }
                    }
                    AppText(text = Res.string.task_no_results, color = LiloExtendedTheme.colors.textSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            } else TaskList(state.tasksResult, !state.isFilterSheetOpen, onAction, Modifier.fillMaxSize(),
                groupLabels = state.categories.filter { it.id != 0L }.associate { it.id to it.title })
        }
    }
    TaskFilterSheet(state, onAction)
}

@AppPreviews
@Composable
private fun TaskListScreenPreview() {
    LiloPreviewWrapper {
        TaskListScreen(state = TaskListState(
            tasksResult = listOf(Task(id = 1, title = "تمرین ساز", priority = 1, category = 3),
                Task(id = 2, title = "ارسال رزومه", priority = 0, category = 2),
                Task(id = 3, title = "خرید هفتگی", priority = 2, category = 4),
                Task(id = 4, title = "مطالعه کتاب", priority = 0, done = true, category = 1)),
        ), onAction = {})
    }
}
