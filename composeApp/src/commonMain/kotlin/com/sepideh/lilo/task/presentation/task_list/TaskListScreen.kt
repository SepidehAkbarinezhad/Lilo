package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppSearchBar
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.domain.Task
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

    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        bodyContainer = {
            TaskListScreen(
                state = state,
                newTask = viewModel.newTask,
                onEvent = viewModel::onEvent
            )
        }
    )

}

@Composable
fun TaskListScreen(
    state: TaskListState,
    newTask: Task?,
    onEvent: (BaseEvent) -> Unit
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
                onClick = { onEvent(BaseEvent.OnNavigateTo(AppDestinations.TaskDetail())) },
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add task"
                )
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
                .statusBarsPadding(),
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

                    if (state.categories.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items = state.categories) { category ->
                                AppText(
                                    modifier = Modifier.widthIn(min = 100.dp).border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(8.dp)
                                    ).padding(4.dp),
                                    text = category.title,
                                    textAlign = TextAlign.Center
                                )
                            }

                        }
                        /* TabRow(
                             selectedTabIndex = state.selectedTabIndex,
                             modifier = Modifier.fillMaxWidth().widthIn(700.dp),
                             indicator = { tabPositions ->
                                 TabRowDefaults.SecondaryIndicator(
                                     modifier = Modifier.tabIndicatorOffset(
                                         tabPositions[state.selectedTabIndex]
                                     )
                                 )
                             }
                         ) {
                             state.categories.forEach { item ->
                                 Tab(
                                     selected = state.selectedTabIndex == item.id,
                                     onClick = { onEvent(TaskListEvent.OnTabSelected(item.id)) },
                                     modifier = Modifier.weight(1f).padding(8.dp),
                                     selectedContentColor = MaterialTheme.colorScheme.primary,
                                     unselectedContentColor = Black.copy(alpha = .5f)
                                 ) {
                                     AppText(
                                         text = item.title,
                                         textType = TextType.SubTitle,
                                         color = Color.Unspecified
                                     )
                                 }
                             }

                         }*/
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
                                                onEvent = onEvent,
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



