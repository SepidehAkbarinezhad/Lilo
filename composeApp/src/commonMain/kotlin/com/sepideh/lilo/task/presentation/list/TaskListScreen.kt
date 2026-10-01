package com.sepideh.lilo.task.presentation.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.BaseListScreen
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DeleteConfirmationDialog
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
            if (state.isDeleteDialogOpen) DeleteConfirmationDialog(logo = Res.drawable.delete_task_logo,
                onConfirm = { viewModel.onAction(TaskListAction.OnDeleteTaskConfirm) },
                onDismiss = { viewModel.onAction(TaskListAction.OnDismissDeleteDialog) })
        })
}

@Composable
fun TaskListScreen(state: TaskListState, isLoading: Boolean = false, onAction: (BaseAction) -> Unit) {
    val accent = LiloExtendedTheme.colors.taskColor
    BaseListScreen(title = Res.string.tasks_list_title, accent = accent,
        searchVisible = state.isSearchVisible, query = state.searchQuery, searchHint = Res.string.search_tasks_action,
        filtersActive = state.taskFilterOption.taskStatus.isNotEmpty() || state.taskFilterOption.priorityList.isNotEmpty(),
        onBack = { onAction(BaseAction.OnNavigateTo(null)); true },
        onSearchVisible = { onAction(TaskListAction.OnSearchToggle(it)) },
        onQueryChange = { onAction(TaskListAction.OnSearchQueryChange(it)) },
        onFilter = { onAction(TaskListAction.OnFilterIcon) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(BaseAction.OnNavigateTo(AppRoutes.Tasks.Detail(null))) }, containerColor = accent, contentColor = Color.Black) {
                Icon(Icons.Outlined.Add, stringResource(Res.string.add_task_label))
            }
        }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            if (state.hasError) Text(stringResource(Res.string.task_operation_error), Modifier.padding(20.dp), color = MaterialTheme.colorScheme.error)
            LazyRow(contentPadding = PaddingValues(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.categories, key = { it.id }) { category ->
                    val id = category.id.takeIf { it != 0L }
                    FilterChip(selected = state.selectedCategory == id, onClick = { onAction(TaskListAction.OnCategorySelected(id)) }, label = { AppText(text = category.title) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accent.copy(alpha = .16f)))
                }
            }
            if (isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = accent)
            if (state.tasksResult.isEmpty() && !isLoading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AppText(text = Res.string.task_no_results, color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else TaskList(state.tasksResult, !state.isFilterSheetOpen, onAction, Modifier.fillMaxSize())
        }
    }
    TaskFilterSheet(state, onAction)
}
