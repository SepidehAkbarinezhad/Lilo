package com.sepideh.lilo.note.presentation.list.components
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.format.localizedDigits
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.note.domain.model.Note
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
@Composable
fun NoteListItem(note: Note, group: String?, onClick: () -> Unit, modifier: Modifier = Modifier) {
 Surface(onClick = onClick, modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp),
  color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha=.55f))) {
  Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
   AppText(text=note.title, textType=TextType.BodyLarge, color=LiloExtendedTheme.colors.title, maxLines=1)
   if (note.content.isNotBlank()) AppText(text=note.content, color=LiloExtendedTheme.colors.textSecondary, maxLines=2)
   if (group != null || note.imageNames.isNotEmpty()) Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(8.dp)) {
    if (group != null) Surface(shape=MaterialTheme.shapes.small, color=MaterialTheme.colorScheme.surfaceVariant) {
     AppText(text=group, modifier=Modifier.padding(horizontal=10.dp, vertical=4.dp), maxLines=1)
    }
    if (note.imageNames.isNotEmpty()) {
     Icon(LiloIcons.Images, null, Modifier.size(16.dp), tint=LiloExtendedTheme.colors.textSupporting)
     AppText(text=stringResource(Res.string.note_images_count,note.imageNames.size).localizedDigits(LocalLayoutDirection.current == LayoutDirection.Rtl), color=LiloExtendedTheme.colors.textSupporting)
    }
   }
  }
 }
}
@AppPreviews @Composable private fun NoteItemPreview() { LiloPreviewWrapper { NoteListItem(Note(title="A small idea",content="Make room for a little creativity every day."),"Personal",{}) } }
