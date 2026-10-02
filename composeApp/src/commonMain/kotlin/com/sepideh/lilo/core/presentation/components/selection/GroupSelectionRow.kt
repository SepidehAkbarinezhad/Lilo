package com.sepideh.lilo.core.presentation.components.selection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*

@Composable
fun GroupSelectionRow(label: String, selected: Boolean, accent: Color, onClick: () -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth().heightIn(min = 64.dp).selectable(selected = selected, role = Role.RadioButton, onClick = onClick).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            AppText(text = label, modifier = Modifier.weight(1f).padding(start = 8.dp), textType = TextType.BodyLarge)
            RadioButton(selected, onClick = null, modifier = Modifier.padding(12.dp), colors = RadioButtonDefaults.colors(selectedColor = accent))
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = .6f))
    }
}
@AppPreviews
@Composable
private fun GroupSelectionRowPreview() {
    LiloPreviewWrapper {
        Column {
            GroupSelectionRow("Personal", true, Color(0xFFFFC107)) {}
            GroupSelectionRow("Work", false, Color(0xFFFFC107)) {}
        }
    }
}
