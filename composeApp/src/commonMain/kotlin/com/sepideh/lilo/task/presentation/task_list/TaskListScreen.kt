package com.sepideh.lilo.task.presentation.task_list

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseHeader
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.BaseScreen
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppSearchBar
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.task.presentation.task_list.components.DeleteConfirmationDialog
import com.sepideh.lilo.task.presentation.task_list.components.TaskFilterSheet
import com.sepideh.lilo.task.presentation.task_list.components.TaskList
import com.sepideh.lilo.ui.theme.LiloColors
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.empty_list
import lilo.composeapp.generated.resources.empty_list_comment
import lilo.composeapp.generated.resources.empty_list_title
import lilo.composeapp.generated.resources.ic_filter
import lilo.composeapp.generated.resources.ic_settings
import lilo.composeapp.generated.resources.ic_search
import lilo.composeapp.generated.resources.tasks_list_title
import org.jetbrains.compose.resources.painterResource


@Composable
fun TaskListScreenRoot(
    viewModel: TaskListViewModel,
    onNavigateTo: (AppRoutes) -> Unit,
    onBack: () -> Boolean
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val baseUiState by viewModel.baseUiStateValue.collectAsStateWithLifecycle()

    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        onBack = onBack,
        bodyContainer = {
            TaskListScreen(
                state = state,
                isLoading = baseUiState.showLoading,
                onAction = viewModel::onAction
            )
        },
        dialogContent = {
            if (state.isDeleteDialogOpen) {
                DeleteConfirmationDialog(onConfirm = {
                    viewModel.onAction(
                        TaskListAction.OnDeleteTaskConfirm
                    )
                    viewModel.onAction(TaskListAction.OnDismissDeleteDialog)
                }, onDismiss = { viewModel.onAction(TaskListAction.OnDismissDeleteDialog) })
            }
        }

    )

}

@Composable
fun TaskListScreen(
    state: TaskListState,
    isLoading: Boolean = false,
    onAction: (BaseAction) -> Unit
) {
    val clickable = !state.isFilterSheetOpen
    val searchResultListState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val palette = LocalLiloColorsPalette.current


    LaunchedEffect(key1 = state.tasksResult) {
        searchResultListState.animateScrollToItem(0)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (clickable) {
                        onAction(
                            BaseAction.OnNavigateTo(
                                (AppRoutes.TaskDetail(taskId = null))
                            )
                        )
                    }

                },
                shape = RoundedCornerShape(20.dp),
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Add task",
                    tint = White
                )
            }
        },
    ) {

        BaseScreen(
            header = {
                TaskListHeader(
                    modifier = Modifier.statusBarsPadding(),
                    state = state,
                    onAction = onAction,
                )
            },
            content = {  if (state.categories.isNotEmpty()) {
                CategoryList(state, palette, clickable, onAction)
            }

                if (state.tasksResult.isEmpty() && !isLoading) {
                    EmptyTaskList(palette)
                } else {
                    TaskList(
                        tasks = state.tasksResult,
                        clickable = clickable,
                        onAction = onAction,
                        modifier = Modifier.fillMaxSize(),
                        scrollState = searchResultListState
                    )
                }}
        )
    }
    TaskFilterSheet(state = state, onAction = onAction)

}

@Composable
fun CategoryList(
    state: TaskListState,
    palette: LiloColors,
    clickable: Boolean,
    onAction: (BaseAction) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = state.categories) { category ->

            // Determine if the category is selected or if it's the first one when selectedCategory is null
            val isSelected =
                category.id == state.selectedCategory || (state.selectedCategory == null && category == state.categories.first())
            val titleColor =
                if (isSelected) palette.selectedCategory else palette.unSelectedCategory

            AppText(
                modifier = Modifier.widthIn(min = 100.dp).border(
                    width = 1.dp,
                    color = titleColor,
                    shape = RoundedCornerShape(8.dp),
                ).padding(4.dp)
                    .clickable(
                        indication = null, // Disable the ripple effect
                        interactionSource = remember { MutableInteractionSource() } // Prevent the ripple interaction
                    ) {
                        if (clickable) onAction(
                            TaskListAction.OnCategorySelected(
                                category.id
                            )
                        )
                    },
                text = category.title,
                textAlign = TextAlign.Center,
                color = titleColor,
                textType = TextType.SubTitle
            )
        }
    }
}

@Composable
fun EmptyTaskList(palette: LiloColors) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Image(
                modifier = Modifier.fillMaxWidth(.5f),
                painter = painterResource(Res.drawable.empty_list),
                contentDescription = null
            )
        }

        AppText(
            text = Res.string.empty_list_title,
            textType = TextType.SubTitle,
            color = palette.primaryContainerTitle
        )
        AppText(
            text = Res.string.empty_list_comment,
            textType = TextType.SubTitle,
            color = palette.primaryTitle
        )
    }
}


@Composable
fun TaskListHeader(
    state: TaskListState,
    onAction: (BaseAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val clickable = !state.isFilterSheetOpen
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = {
                    focusManager.clearFocus()
                    onAction(TaskListAction.OnSearchToggle(false))
                    if (clickable) onAction(TaskListAction.OnFilterIcon)
                },
                enabled = !state.isFilterSheetOpen,
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_filter),
                    contentDescription = null
                )
            }

            AnimatedContent(
                targetState = state.isSearchVisible,
                label = "search-bar-animation"
            ) { isSearchVisible ->
                if (isSearchVisible) {
                    AppSearchBar(
                        focusRequester = focusRequester,
                        searchQuery = state.searchQuery,
                        onSearchQueryChange = {
                            if (clickable) {
                                onAction(TaskListAction.OnSearchQueryChange(it))
                            }
                        },
                        onClose = {
                            onAction(TaskListAction.OnSearchToggle(false))
                            keyboardController?.hide()
                        },
                        readonly = !clickable
                    )

                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painterResource(Res.drawable.ic_search),
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onAction(TaskListAction.OnSearchToggle(true))
                            },
                            contentDescription = "Open Search",
                        )
                        BaseHeader(modifier= Modifier.weight(1f),title = Res.string.tasks_list_title, mainScreen = true)

                    }
                }
            }
        }

        IconButton(
            onClick = {
                focusManager.clearFocus()
                onAction(TaskListAction.OnSearchToggle(false))
                onAction(BaseAction.OnNavigateTo(AppRoutes.Settings)) },
            enabled = !state.isFilterSheetOpen,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_settings),
                contentDescription = "Open setting"
            )
        }
    }
}




