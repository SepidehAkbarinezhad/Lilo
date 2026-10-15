package com.sepideh.lilo.note.presentation.list.components
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.core.presentation.components.LiloSelectionChip
import com.sepideh.lilo.note.presentation.list.*
import com.sepideh.lilo.note.presentation.list.model.NoteSortOrder
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
@OptIn(ExperimentalMaterial3Api::class) @Composable
fun NoteFilterSheet(state: NoteListState, action:(BaseAction)->Unit) {
 val accent=LiloExtendedTheme.colors.noteColor
 ModalBottomSheet(onDismissRequest={action(NoteListAction.DismissFilters)},containerColor=MaterialTheme.colorScheme.background) {
  Column(Modifier.fillMaxWidth().padding(horizontal=20.dp).padding(bottom=24.dp)) {
   SheetHeader(Res.string.filter_label,Res.string.confirm_button,accent,onConfirm={action(NoteListAction.ApplyFilters)},onReset={action(NoteListAction.ResetFilters)},resetActionColor=LiloExtendedTheme.colors.noteAction)
   listOf(NoteSortOrder.Date to Res.string.note_recent_sort,NoteSortOrder.Title to Res.string.note_title_sort).forEach { (sort,label) ->
    LiloSelectionChip(label=stringResource(label), selected=state.draftSort==sort, accent=accent,
     onClick={action(NoteListAction.DraftSort(sort))})
   }
  }
 }
}

@AppPreviews @Composable private fun NoteFiltersPreview() {
 LiloPreviewWrapper { NoteFilterSheet(NoteListState(draftSort=NoteSortOrder.Title), {}) }
}
