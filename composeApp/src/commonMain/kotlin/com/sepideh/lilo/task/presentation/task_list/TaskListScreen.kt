package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.core.presentation.components.AppSearchBar
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.TextType
import com.sepideh.lilo.task.domain.Task
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.all_tasks
import org.jetbrains.compose.resources.stringResource


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
            onSearchQueryChange = { onAction(TaskListAction.OnSearchQueryChange(it)) },
            onImeSearch = { keyboardController?.hide() })
        Surface(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TabRow(
                    selectedTabIndex = state.selectedTabIndex,
                    modifier = Modifier.fillMaxWidth().widthIn(700.dp),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = Yellow,
                            modifier = Modifier.tabIndicatorOffset(
                                tabPositions[state.selectedTabIndex]
                            )
                        )
                    }
                ) {
                    Tab(
                        selected = state.selectedTabIndex == 0,
                        onClick = { onAction(TaskListAction.OnTabSelected(0)) },
                        modifier = Modifier.weight(1f),
                        selectedContentColor = Yellow,
                        unselectedContentColor = Black.copy(alpha = .5f)
                    ) {
                        AppText(
                            text = stringResource(Res.string.all_tasks),
                            textType = TextType.SubTitle,
                            color = Color.Unspecified
                        )
                    }
                    Tab(
                        selected = state.selectedTabIndex == 1,
                        onClick = { onAction(TaskListAction.OnTabSelected(0)) },
                        modifier = Modifier.weight(1f),
                        selectedContentColor = Yellow,
                        unselectedContentColor = Black.copy(alpha = .5f)
                    ) {
                        AppText(
                            text = stringResource(Res.string.all_tasks),
                            textType = TextType.SubTitle,
                            modifier = Modifier.padding(12.dp),
                            color = Color.Unspecified
                        )
                    }
                }
            }
        }
    }
}


