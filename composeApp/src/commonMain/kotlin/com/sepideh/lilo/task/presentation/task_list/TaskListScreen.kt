package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.core.presentation.components.AppSearchBar
import com.sepideh.lilo.task.domain.Task


@Composable
fun TaskListScreenRoot(
    viewModel: TaskListViewModel,
    onTaskClicked: (Task) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskListScreen(state = state, onAction = { action ->
        when (action) {
            is TaskListAction.OnTaskClick -> onTaskClicked(action.task)
            else -> Unit
        }
        viewModel.onAction(action)
    })
}

@Composable
fun TaskListScreen(
    state: TaskListState,
    onAction: (TaskListAction) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Blue).statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppSearchBar(
            modifier = Modifier.fillMaxWidth().width(400.dp).padding(16.dp),
            searchQuery = state.searchQuery,
            onSearchQueryChange = {onAction(TaskListAction.OnSearchQueryChange(it))},
            onImeSearch = {keyboardController?.hide()})
    }
}


