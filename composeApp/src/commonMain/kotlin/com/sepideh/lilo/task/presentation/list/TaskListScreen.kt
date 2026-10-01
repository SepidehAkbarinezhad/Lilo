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
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        topBar = { TaskListHeader(state, onAction) },
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
                    FilterChip(selected = state.selectedCategory == id, onClick = { onAction(TaskListAction.OnCategorySelected(id)) }, label = { Text(category.title) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = accent.copy(alpha = .16f)))
                }
            }
            if (isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = accent)
            if (state.tasksResult.isEmpty() && !isLoading) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.task_no_results), color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else TaskList(state.tasksResult, !state.isFilterSheetOpen, onAction, Modifier.fillMaxSize())
        }
    }
    TaskFilterSheet(state, onAction)
}

@Composable
fun TaskListHeader(state: TaskListState, onAction: (BaseAction) -> Unit, modifier: Modifier = Modifier) {
    val accent = LiloExtendedTheme.colors.taskColor
    Row(modifier.fillMaxWidth().statusBarsPadding().heightIn(min = 64.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onAction(BaseAction.OnNavigateTo(null)) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(Res.string.cancel_button), Modifier.size(20.dp)) }
        if (state.isSearchVisible) {
            TextField(value = state.searchQuery, onValueChange = { onAction(TaskListAction.OnSearchQueryChange(it)) }, singleLine = true,
                modifier = Modifier.weight(1f), placeholder = { Text(stringResource(Res.string.search_tasks_action)) },
                colors = TextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.background, unfocusedContainerColor = MaterialTheme.colorScheme.background, focusedIndicatorColor = accent),
                trailingIcon = { IconButton(onClick = { onAction(TaskListAction.OnSearchToggle(false)) }) { Icon(Icons.Outlined.Close, stringResource(Res.string.cancel_button), Modifier.size(20.dp)) } })
        } else {
            Text(stringResource(Res.string.tasks_list_title), Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { onAction(TaskListAction.OnSearchToggle(true)) }) { Icon(Icons.Outlined.Search, stringResource(Res.string.search_tasks_action), Modifier.size(20.dp)) }
        }
        IconButton(onClick = { onAction(TaskListAction.OnFilterIcon) }) {
            Icon(Icons.Outlined.Tune, stringResource(Res.string.filter_label), Modifier.size(20.dp),
                tint = if (state.taskFilterOption.taskStatus.isNotEmpty() || state.taskFilterOption.priorityList.isNotEmpty()) accent else MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
