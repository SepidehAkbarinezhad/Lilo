package com.sepideh.lilo.note.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseRoot
import com.sepideh.lilo.core.presentation.components.AppBackHandler
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.core.presentation.components.form.BottomActionFormScreen
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.domain.model.NoteGroup
import com.sepideh.lilo.note.presentation.toGroupOptions
import com.sepideh.lilo.note.presentation.components.NoteTopAppBar
import com.sepideh.lilo.note.presentation.view.components.NoteViewContent
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSpacing
import com.sepideh.lilo.ui.theme.LocalLiloAppLanguage
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.note_detail_title
import lilo.composeapp.generated.resources.note_edit_action
import lilo.composeapp.generated.resources.note_not_found
import lilo.composeapp.generated.resources.note_operation_error
import lilo.composeapp.generated.resources.note_retry
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoteViewScreenRoot(
    noteId: Long,
    viewModel: NoteViewViewModel,
    onNavigateTo: (AppRoutes) -> Unit,
    onBack: () -> Boolean,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    // The existing editor pops after saving. Reload on resume, including after restoration.
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.onAction(NoteViewAction.Load(noteId))
    }
    AppBackHandler { viewModel.onAction(NoteViewAction.Back) }
    BaseRoot(
        viewModel = viewModel,
        navigateTo = onNavigateTo,
        onBack = onBack,
        bodyContainer = { NoteViewScreen(state, viewModel::onAction) },
    )
}

@Composable
fun NoteViewScreen(state: NoteViewState, onAction: (NoteViewAction) -> Unit) {
    val colors = LiloExtendedTheme.colors.notes
    BottomActionFormScreen(
        title = Res.string.note_detail_title,
        featureColors = colors,
        saveEnabled = state.canEdit,
        isSaving = false,
        actionLabel = Res.string.note_edit_action,
        actionIcon = LiloIcons.Edit,
        topBar = {
            NoteTopAppBar(
                title = state.note?.title ?: stringResource(Res.string.note_detail_title),
                onBack = { onAction(NoteViewAction.Back) },
                selectableTitle = state.note != null,
            )
        },
        onBack = { onAction(NoteViewAction.Back); true },
        onSave = { onAction(NoteViewAction.Edit) },
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = LiloSpacing.Screen)
                .padding(top = LiloSpacing.Section, bottom = LiloSpacing.Small),
            verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item),
        ) {
            if (state.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth(), color = colors.accent)
            if (state.hasError || state.noteMissing) {
                AppText(
                    text = if (state.noteMissing) Res.string.note_not_found else Res.string.note_operation_error,
                    color = MaterialTheme.colorScheme.error,
                )
                TextButton(onClick = { onAction(NoteViewAction.Retry) }) {
                    AppText(text = Res.string.note_retry)
                }
            }
            val groupLabel = listOfNotNull(state.group).toGroupOptions(LocalLiloAppLanguage.current).firstOrNull()?.label
            state.note?.let { NoteViewContent(it, groupLabel) }
        }
    }
}

@AppPreviews
@Composable
private fun NoteViewPopulatedPreview() {
    LiloPreviewWrapper {
        NoteViewScreen(
            NoteViewState(
                isLoading = false,
                group = NoteGroup(id = 7, titleEn = "Ideas", titleFa = "ایده‌ها"),
                note = Note(
                    id = 1,
                    title = "A little inspiration — ایده‌های کوچک برای روزهای آینده",
                    content = "Ideas worth keeping.\n\nیادداشت‌هایی برای روزهای آرام‌تر.\n".repeat(12),
                    imageNames = listOf("sample.jpg", "another.jpg"),
                    groupId = 7,
                    updatedAt = 1_794_000_000_000L,
                ),
            ),
            {},
        )
    }
}

@AppPreviews
@Composable
private fun NoteViewTitleOnlyPreview() {
    LiloPreviewWrapper {
        NoteViewScreen(
            NoteViewState(note = Note(id = 1, title = "A simple note", createdAt = 1_794_000_000_000L), isLoading = false),
            {},
        )
    }
}

@AppPreviews
@Composable
private fun NoteViewMissingPreview() {
    LiloPreviewWrapper {
        NoteViewScreen(NoteViewState(isLoading = false, noteMissing = true), {})
    }
}
