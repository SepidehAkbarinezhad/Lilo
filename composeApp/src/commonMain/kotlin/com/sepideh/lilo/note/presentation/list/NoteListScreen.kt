package com.sepideh.lilo.note.presentation.list
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.*
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.note.presentation.list.components.*
import com.sepideh.lilo.note.presentation.list.model.NoteSortOrder
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
@Composable fun NoteListScreenRoot(viewModel:NoteListViewModel,onNavigateTo:(AppRoutes)->Unit,onBack:()->Boolean) {
 val state by viewModel.state.collectAsStateWithLifecycle()
 BaseRoot(viewModel=viewModel,navigateTo=onNavigateTo,onBack=onBack,bodyContainer={NoteListScreen(state,viewModel::onAction,onBack)},dialogContent={
  if(state.isSortSheetOpen) NoteSortSheet(state,viewModel::onAction)
  if(state.isDeleteDialogOpen) DeleteConfirmationDialog(accent=LiloExtendedTheme.colors.noteColor,
   title=stringResource(Res.string.note_delete_title),message=stringResource(Res.string.delete_item_confirmation),
   onConfirm={viewModel.onAction(NoteListAction.ConfirmDelete)},onDismiss={viewModel.onAction(NoteListAction.DismissDelete)})
 })
}
@Composable fun NoteListScreen(state:NoteListState,action:(BaseAction)->Unit,onBack:()->Boolean) {
 val accent=LiloExtendedTheme.colors.noteColor
 BaseListScreen(title=Res.string.note_feature_title,accent=accent,referenceStyle=true,
  searchVisible=state.isSearchVisible,query=state.searchQuery,searchHint=Res.string.note_search_hint,
  filtersActive=state.sortOrder!=NoteSortOrder.Date,
  filterIcon=LiloIcons.Sort,filterLabel=Res.string.note_sort_title,
  onBack=onBack,onSearchVisible={action(NoteListAction.SearchVisible(it))},onQueryChange={action(NoteListAction.Search(it))},onFilter={action(NoteListAction.OpenSort)},
  floatingActionButton={FloatingActionButton(onClick={action(BaseAction.OnNavigateTo(AppRoutes.Notes.Detail(null)))},shape=CircleShape,containerColor=accent,modifier=Modifier.size(64.dp)) { Icon(LiloIcons.Add,stringResource(Res.string.add_note_title),Modifier.size(32.dp),tint=LiloExtendedTheme.colors.onNoteAccent) }}) { padding ->
  Column(Modifier.fillMaxSize().padding(padding)) {
    val options=listOf(null to stringResource(Res.string.all),0L to stringResource(Res.string.no_group_label))+state.groups.map { it.id to it.label }
   LazyRow(contentPadding=PaddingValues(horizontal=20.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
    items(options,key={it.first?:-1L}) { (id,label) ->
     LiloSelectionChip(label=label, selected=state.selectedGroup==id, accent=accent,
      onClick={action(NoteListAction.SelectGroup(id))})
    }
   }
   if(state.isLoading||state.isDeleting) LinearProgressIndicator(Modifier.fillMaxWidth(),color=accent)
   if(state.hasError) Column(Modifier.padding(horizontal=20.dp)) {
    AppText(text=Res.string.note_operation_error,color=MaterialTheme.colorScheme.error)
    TextButton(onClick={action(NoteListAction.Retry)}) { Text(stringResource(Res.string.note_retry)) }
   }
   if (!state.isLoading && state.notesResult.isEmpty()) {
    val filtered = state.searchQuery.isNotBlank() || state.selectedGroup != null
    ListEmptyState(if (filtered) Res.string.note_no_results else Res.string.home_no_notes, accent, icon = vectorResource(Res.drawable.home_note))
   } else NoteList(state,action,Modifier.weight(1f))
  }
 }
}
@AppPreviews @Composable private fun NotesEmptyPreview() { LiloPreviewWrapper { NoteListScreen(NoteListState(isLoading=false),{}, {true}) } }

@AppPreviews
@Composable
private fun NotesPopulatedPreview() {
 LiloPreviewWrapper {
  NoteListScreen(NoteListState(isLoading=false, notesResult=listOf(
   com.sepideh.lilo.note.domain.model.Note(id=1,title="A little inspiration",content="Ideas worth keeping for another day.",groupId=1),
   com.sepideh.lilo.note.domain.model.Note(id=2,title="Weekend recipe",content="A few ingredients and a little time.",imageNames=listOf("sample.jpg"))),
   groups=listOf(com.sepideh.lilo.core.presentation.components.group.GroupOption(1,"Personal"))),{}, {true})
 }
}
