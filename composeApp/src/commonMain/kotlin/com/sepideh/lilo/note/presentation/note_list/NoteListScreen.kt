package com.sepideh.lilo.note.presentation.note_list

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.BaseHeader
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.BaseScreen
import com.sepideh.lilo.core.presentation.components.AppHeader
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.AppSearchBar
import com.sepideh.lilo.core.presentation.components.DeleteConfirmationDialog
import com.sepideh.lilo.core.presentation.components.FeatureEmptyIcon
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.note.presentation.note_list.components.NoteList
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.delete_task_logo
import lilo.composeapp.generated.resources.ic_search
import lilo.composeapp.generated.resources.ic_settings
import lilo.composeapp.generated.resources.note_feature_title
import org.jetbrains.compose.resources.painterResource

@Composable
fun NoteListScreenRoot(
    viewModel: NoteListViewModel,
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
            NoteListScreen(
                state = state,
                isLoading = baseUiState.showLoading,
                onAction = viewModel::onAction
            )
        },
        dialogContent = {
            if (state.isDeleteDialogOpen) {
                DeleteConfirmationDialog(
                    logo = Res.drawable.delete_task_logo,
                    onConfirm = {
                        viewModel.onAction(NoteListAction.OnDeleteNoteConfirm)
                        viewModel.onAction(NoteListAction.OnDismissDeleteDialog)
                    },
                    onDismiss = { viewModel.onAction(NoteListAction.OnDismissDeleteDialog) }
                )
            }
        }
    )
}

@Composable
fun NoteListScreen(
    state: NoteListState,
    isLoading: Boolean = false,
    onAction: (BaseAction) -> Unit
) {
    LaunchedEffect(key1 = state.notesResult) {
        // list re-renders when notesResult changes; add scroll reset here if you
        // give NoteList a scrollState like TaskList has
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(
                        BaseAction.OnNavigateTo(AppRoutes.Notes.Detail(noteId = null))
                    )
                },
                shape = RoundedCornerShape(20.dp),
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add note", tint = White)
            }
        },
    ) {
        BaseScreen(
            header = {
                NoteListHeader(
                    modifier = Modifier.statusBarsPadding(),
                    state = state,
                    onAction = onAction,
                )
            },
            content = {
                if (state.categories.isNotEmpty()) {
                    // NOTE: com.sepideh.lilo.task.presentation.task_list.CategoryList takes
                    // TaskListState/TaskListAction today — extract a shared version keyed on
                    // (categories, selectedCategory, onCategorySelected) before wiring this up.
                }

                if (state.notesResult.isEmpty() && !isLoading) {
                    FeatureEmptyIcon(feature = LiloFeature.NOTES)
                } else {
                    NoteList(
                        notes = state.notesResult,
                        onAction = onAction,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        )
    }
}

@Composable
fun NoteListHeader(
    state: NoteListState,
    onAction: (BaseAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    AppHeader {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            AnimatedContent(
                targetState = state.isSearchVisible,
                label = "note-search-bar-animation"
            ) { isSearchVisible ->
                if (isSearchVisible) {
                    AppSearchBar(
                        focusRequester = focusRequester,
                        searchQuery = state.searchQuery,
                        onSearchQueryChange = { onAction(NoteListAction.OnSearchQueryChange(it)) },
                        onClose = {
                            onAction(NoteListAction.OnSearchToggle(false))
                            keyboardController?.hide()
                        },
                        readonly = false
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painterResource(Res.drawable.ic_search),
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onAction(NoteListAction.OnSearchToggle(true)) },
                            contentDescription = "Open Search",
                        )
                        BaseHeader(
                            modifier = Modifier.weight(1f),
                            title = Res.string.note_feature_title,
                            mainScreen = true
                        )
                    }
                }
            }
        }

        IconButton(
            onClick = {
                focusManager.clearFocus()
                onAction(NoteListAction.OnSearchToggle(false))
                onAction(BaseAction.OnNavigateTo(AppRoutes.Settings))
            },
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_settings),
                contentDescription = "Open setting"
            )
        }
    }
}

@AppPreviews
@Composable
fun NoteListScreenPrev() {
    LiloPreviewWrapper {
        NoteListScreen(
            state = NoteListState(),
            isLoading = false,
            onAction = {}
        )
    }
}