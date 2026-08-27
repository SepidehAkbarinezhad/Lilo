package com.sepideh.lilo.note.presentation.note_list.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.note.presentation.note_list.NoteListAction
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NoteListItem(
    modifier: Modifier = Modifier,
    note: Note,
    onAction: (BaseAction) -> Unit
) {
    Card(
        modifier = modifier, shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        with(note) {
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                        .width(4.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(color = LiloExtendedTheme.colors.noteColor)
                )
                Column(
                    Modifier.weight(.8f).padding(vertical = 12.dp, horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AppText(
                        text = title,
                        textType = TextType.SubTitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    AppText(
                        text = content,
                        textType = TextType.Body,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.9f)
                    )
                }
                IconButton(onClick = {
                    onAction(NoteListAction.OnDeleteNoteIcon(note = note))
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "delete Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun NoteListItemPreview() {
    NoteListItem(
        note = Note(
            id = 0,
            title = "title",
            content = "This is a preview of note content that might wrap onto a second line",
            createdAt = 0L,
            updatedAt = 0L
        )
    ) { }
}