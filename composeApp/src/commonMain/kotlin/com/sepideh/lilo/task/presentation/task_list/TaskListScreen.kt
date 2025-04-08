package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppDestinations
import com.sepideh.lilo.core.presentation.BaseEvent
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppImageFromResource
import com.sepideh.lilo.core.presentation.components.AppSearchBar
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.DialogModel
import com.sepideh.lilo.task.presentation.reminder.DeleteConfirmationDialog
import com.sepideh.lilo.task.presentation.task_list.components.TaskList
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.empty_list_comment
import lilo.composeapp.generated.resources.empty_list_title


@Composable
fun TaskListScreenRoot(
    viewModel: TaskListViewModel,
    onNavigateTo: (AppDestinations) -> Unit
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiStateValue.collectAsStateWithLifecycle()

    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        bodyContainer = {
            TaskListScreen(
                state = state,
                isLoading = baseUiState.showLoading,
                onEvent = viewModel::onEvent
            )
        },
        dialogModel = DialogModel(content = {
            DeleteConfirmationDialog(onConfirm = {
                viewModel.onEvent(
                    TaskListEvent.OnDeleteTaskConfirm
                )
                viewModel.onEvent(BaseEvent.ShowDialog(false))
            }, onDismiss = {
                viewModel.onEvent(BaseEvent.ShowDialog(false))
            })
        }, onDismissRequest = { viewModel.onEvent(BaseEvent.ShowDialog(false)) })
    )

}

@Composable
fun TaskListScreen(
    state: TaskListState,
    isLoading: Boolean = false,
    onEvent: (BaseEvent) -> Unit
) {
    println("TaskListScreen  ${state.categories}")
    val keyboardController = LocalSoftwareKeyboardController.current
    val pagerState = rememberPagerState { 2 }
    val searchResultListState = rememberLazyListState()

    LaunchedEffect(key1 = state.tasksResult) {
        searchResultListState.animateScrollToItem(0)
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    if (state.categories.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(items = state.categories) { category ->

                                // Determine if the category is selected or if it's the first one when selectedCategory is null
                                val isSelected =
                                    category.id == state.selectedCategory || (state.selectedCategory == null && category == state.categories.first())
                                val selectedColor =
                                    if (isSelected) MaterialTheme.colorScheme.primary else Gray

                                AppText(
                                    modifier = Modifier.widthIn(min = 100.dp).border(
                                        width = 1.dp,
                                        color = selectedColor,
                                        shape = RoundedCornerShape(8.dp)
                                    ).padding(4.dp)
                                        .clickable(indication = null, // Disable the ripple effect
                                            interactionSource = remember { MutableInteractionSource() } // Prevent the ripple interaction
                                        ) { onEvent(TaskListEvent.OnCategorySelected(category.id)) },
                                    text = category.title,
                                    textAlign = TextAlign.Center,
                                    color = selectedColor,
                                    textType = TextType.SubTitle
                                )
                            }
                        }
                    }


                    if (state.tasksResult.isEmpty() && !isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AppImageFromResource()
                            AppText(
                                text = Res.string.empty_list_title,
                                textType = TextType.SubTitle,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            AppText(
                                text = Res.string.empty_list_comment,
                                textType = TextType.SubTitle,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        TaskList(
                            tasks = state.tasksResult,
                            onEvent = onEvent,
                            modifier = Modifier.fillMaxSize(),
                            scrollState = searchResultListState
                        )
                    }
                }
            }
        }
    }


}



