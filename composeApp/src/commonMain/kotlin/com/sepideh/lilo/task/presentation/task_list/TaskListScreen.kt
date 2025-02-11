package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.core.presentation.components.AppSearchBar
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.TextType
import com.sepideh.lilo.task.presentation.task_list.components.TaskList
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.all_tasks
import lilo.composeapp.generated.resources.no_result
import org.jetbrains.compose.resources.stringResource


@Composable
fun TaskListScreenRoot(
    viewModel: TaskListViewModel,
    onNavigateTo: (AppDestinations) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaskListScreen(state = state, onEvent = { action ->
        viewModel.onAction(action)
    })
}

@Composable
fun TaskListScreen(
    state: TaskListState,
    onEvent: (TaskListEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val pagerState = rememberPagerState { 2 }
    val searchResultListState = rememberLazyListState()

    LaunchedEffect(key1 = state.searchResults) {
        searchResultListState.animateScrollToItem(0)
    }
    LaunchedEffect(state.selectedTabIndex) {
        //when click on tabs,switch the pager
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        //when switch the pager,change selected tab
        onEvent(TaskListEvent.OnTabSelected(pagerState.currentPage))
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(TaskListEvent.OnAddNewTaskClick) },
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Rounded.Add,
                    contentDescription = "Add task")
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Blue).statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppSearchBar(
                modifier = Modifier.fillMaxWidth().width(400.dp).padding(16.dp),
                searchQuery = state.searchQuery,
                onSearchQueryChange = { onEvent(TaskListEvent.OnSearchQueryChange(it)) },
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
                            onClick = { onEvent(TaskListEvent.OnTabSelected(0)) },
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
                            onClick = { onEvent(TaskListEvent.OnTabSelected(1)) },
                            modifier = Modifier.weight(1f),
                            selectedContentColor = Yellow,
                            unselectedContentColor = Black.copy(alpha = .5f)
                        ) {
                            AppText(
                                text = "2",
                                textType = TextType.SubTitle,
                                modifier = Modifier.padding(12.dp),
                                color = Color.Unspecified
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) { pageIndex ->
                        Box(Modifier.fillMaxSize()) {
                            when (pageIndex) {
                                0 -> {
                                    when {
                                        state.isLoading -> {}
                                        state.searchResults.isEmpty() -> {
                                            AppText(
                                                text = stringResource(Res.string.no_result),
                                                textType = TextType.SubTitle,
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }

                                        else -> {
                                            TaskList(
                                                tasks = state.searchResults,
                                                onTaskClick = { onEvent(TaskListEvent.OnSelectTask(it)) },
                                                modifier = Modifier.fillMaxSize(),
                                                scrollState = searchResultListState
                                            )
                                        }
                                    }

                                }

                                1 -> {
                                    AppText(text = "empty", textType = TextType.Body)
                                }
                            }
                        }
                    }
                }
            }
        }
    }



}



