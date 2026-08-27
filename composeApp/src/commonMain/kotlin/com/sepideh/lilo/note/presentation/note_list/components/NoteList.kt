package com.sepideh.lilo.note.presentation.note_list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.note.domain.model.Note

@Composable
fun NoteList(
    notes: List<Note>,
    onAction: (BaseAction) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        state = scrollState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(top = 12.dp, bottom = 54.dp)
    ) {
        items(items = notes, key = { it.id }) { note ->
            NoteListItem(
                modifier = Modifier.fillMaxWidth().clickable {
                    onAction(
                        BaseAction.OnNavigateTo(
                            AppRoutes.Notes.Detail(noteId = note.id)
                        )
                    )
                }.padding(horizontal = 12.dp),
                note = note,
                onAction = onAction
            )
        }
    }
}