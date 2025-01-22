package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.task.domain.Task
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun TaskListScreenRoot(
    viewModel: TaskListViewModel,
    onTaskClicked: (Task) -> Unit
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskListScreen(state = state, onAction = { action ->
        when(action){
            is TaskListAction.OnTaskClick->onTaskClicked(action.task)
            else->Unit
        }
        viewModel.onAction(action) })
}

class TaskListScreen(
    state: TaskListState,
    onAction: (TaskListAction) -> Unit
) {

}


