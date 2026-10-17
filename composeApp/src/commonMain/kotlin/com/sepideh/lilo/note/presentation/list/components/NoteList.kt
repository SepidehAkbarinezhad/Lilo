package com.sepideh.lilo.note.presentation.list.components
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.sepideh.lilo.ui.theme.LocalLiloAppLanguage
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.gesture.SwipeToRevealDelete
import com.sepideh.lilo.note.presentation.list.*
@Composable fun NoteList(state: NoteListState, onAction: (BaseAction)->Unit, modifier: Modifier=Modifier) {
 val listState = rememberLazyListState()
 val sortKey = "${state.sortOrder.name}:${LocalLiloAppLanguage.current.name}"
 var appliedSortKey by rememberSaveable { mutableStateOf(sortKey) }
 LaunchedEffect(sortKey) {
  if (appliedSortKey != sortKey) {
   // Override stable-item anchoring when the order changes, but retain position on return.
   listState.requestScrollToItem(0)
   appliedSortKey = sortKey
  }
 }
 LazyColumn(modifier, state=listState, contentPadding=PaddingValues(start=20.dp,end=20.dp,top=12.dp,bottom=96.dp),verticalArrangement=Arrangement.spacedBy(14.dp)) {
  items(state.notesResult,key={it.id}) { note ->
   SwipeToRevealDelete(enabled=!state.isDeleting,onDelete={onAction(NoteListAction.OnDeleteNoteIcon(note))}) {
    NoteListItem(note,state.groups.firstOrNull { it.id==note.groupId }?.label,
     onClick={onAction(BaseAction.OnNavigateTo(AppRoutes.Notes.Detail(note.id)))})
   }
  }
 }
}
