package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.format.localizedDigits
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Shared placement for group and image metadata; feature-specific details can follow. */
@Composable
fun ItemMetadata(groupLabel: String?, imageCount: Int, modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val reminderMaxWidth = maxWidth * .5f
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (!groupLabel.isNullOrBlank()) Surface(modifier = Modifier.weight(1f, fill = false).widthIn(min = 64.dp, max = 128.dp),
                shape = MaterialTheme.shapes.small, color = MaterialTheme.colorScheme.surfaceVariant) {
                AppText(text = groupLabel, textType = TextType.Body, maxLines = 1, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (imageCount > 0) Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(LiloIcons.Images, stringResource(Res.string.attachment_count, imageCount),
                    Modifier.size(16.dp), tint = LiloExtendedTheme.colors.textSupporting)
                AppText(text = imageCount.toString().localizedDigits(LocalLayoutDirection.current == LayoutDirection.Rtl),
                    textType = TextType.Caption, color = LiloExtendedTheme.colors.textSupporting)
            }
        }
        if (trailing != null) Box(Modifier.widthIn(max = reminderMaxWidth), contentAlignment = Alignment.CenterEnd) {
            trailing()
        }
        }
    }
}
