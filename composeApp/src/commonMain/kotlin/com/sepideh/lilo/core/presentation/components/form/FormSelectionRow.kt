package com.sepideh.lilo.core.presentation.components.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*

@Composable
fun FormSelectionRow(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClear: (() -> Unit)? = null,
    clearContentDescription: String? = null,
) {
    Surface(onClick = onClick, modifier = modifier, enabled = enabled, shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.background,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = .6f))) {
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            AppText(text = title, modifier = Modifier.weight(1f), textType = TextType.BodyLarge)
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = .045f)) {
                AppText(text = value, modifier = Modifier.widthIn(max = 172.dp).padding(horizontal = 12.dp, vertical = 8.dp),
                    textType = TextType.Body, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
            }
            if (onClear != null) IconButton(onClick = onClear, enabled = enabled) { Icon(Icons.Outlined.Close, clearContentDescription, Modifier.size(18.dp)) }
        }
    }
}

@AppPreviews
@Composable
private fun FormSelectionRowPreview() {
    LiloPreviewWrapper {
        FormSelectionRow(Icons.Outlined.FolderOpen, "Group", "Personal", {})
    }
}
