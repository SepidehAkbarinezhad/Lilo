package com.sepideh.lilo.note.presentation.view.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.CreationDateCaption
import com.sepideh.lilo.core.presentation.components.ItemMetadata
import com.sepideh.lilo.core.presentation.components.images.ImageAttachments
import com.sepideh.lilo.core.presentation.images.AttachmentPreview
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSpacing
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.last_modified_on_date

@Composable
fun NoteViewContent(note: Note, groupLabel: String?, modifier: Modifier = Modifier) {
    Column(modifier) {
        if (!groupLabel.isNullOrBlank() || note.lastModifiedAt > 0) {
            ItemMetadata(
                groupLabel = groupLabel,
                imageCount = 0,
                trailing = if (note.lastModifiedAt > 0) ({
                    CreationDateCaption(note.lastModifiedAt, label = Res.string.last_modified_on_date)
                }) else null,
            )
            if (note.content.isNotEmpty() || note.imageNames.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = LiloSpacing.Item),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            if (note.content.isNotEmpty()) {
                // Keep the full body composed so copying includes text outside the viewport.
                SelectionContainer {
                    AppText(
                        text = note.content,
                        textType = TextType.Body,
                        color = LiloExtendedTheme.colors.textPrimary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            if (note.imageNames.isNotEmpty()) {
                ImageAttachments(
                    images = note.imageNames.map { AttachmentPreview(it, stored = true) },
                    accent = LiloExtendedTheme.colors.notes.accent,
                    showHeader = false,
                )
            }
        }
    }
}
