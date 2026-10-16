package com.sepideh.lilo.note.presentation.list.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.note.presentation.list.*
import com.sepideh.lilo.note.presentation.list.model.NoteSortOrder
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSortSheet(state: NoteListState, action: (BaseAction) -> Unit) {
    ModalBottomSheet(
        onDismissRequest = { action(NoteListAction.DismissSort) },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        NoteSortContent(state, action)
    }
}

@Composable
private fun NoteSortContent(state: NoteListState, action: (BaseAction) -> Unit) {
    val colors = LiloExtendedTheme.colors
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        .padding(horizontal = 28.dp).padding(bottom = 64.dp)) {
        SheetHeader(Res.string.note_sort_title, Res.string.apply_label, colors.noteColor,
            onConfirm = { action(NoteListAction.ApplySort) })
        Column(Modifier.selectableGroup()) {
            NoteSortOption(Res.string.note_recent_sort, Res.string.note_recent_sort_description,
                state.draftSort == NoteSortOrder.Date) { action(NoteListAction.DraftSort(NoteSortOrder.Date)) }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            NoteSortOption(Res.string.note_title_sort, Res.string.note_title_sort_description,
                state.draftSort == NoteSortOrder.Title) { action(NoteListAction.DraftSort(NoteSortOrder.Title)) }
        }
    }
}

@Composable
private fun NoteSortOption(title: StringResource, description: StringResource, selected: Boolean, onClick: () -> Unit) {
    val colors = LiloExtendedTheme.colors
    Row(Modifier.fillMaxWidth().heightIn(min = 72.dp)
        .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
        .padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        RadioButton(selected = selected, onClick = null,
            colors = RadioButtonDefaults.colors(selectedColor = colors.noteAction.copy(alpha = .75f), unselectedColor = colors.textSecondary))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            AppText(text = title, textType = TextType.SectionTitle)
            AppText(text = description, textType = TextType.Body, color = colors.textSecondary)
        }
    }
}

@AppPreviews
@Composable
private fun NoteSortRecentPreview() {
    LiloPreviewWrapper { NoteSortContent(NoteListState(), {}) }
}

@AppPreviews
@Composable
private fun NoteSortTitlePreview() {
    LiloPreviewWrapper { NoteSortContent(NoteListState(draftSort = NoteSortOrder.Title), {}) }
}
