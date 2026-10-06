package com.sepideh.lilo.core.presentation.components.group

import androidx.compose.foundation.BorderStroke
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
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.*

@Composable
fun GroupSelectionRow(label: String, selected: Boolean, accent: Color, enabled: Boolean = true, onClick: () -> Unit) {
    Surface(shape = MaterialTheme.shapes.medium,
        color = if (selected) accent.copy(alpha = .08f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) accent.copy(alpha = .6f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = .55f))) {
        Row(Modifier.fillMaxWidth().heightIn(min = LiloSize.SelectionRow)
            .selectable(selected = selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = LiloSpacing.Small), verticalAlignment = Alignment.CenterVertically) {
            AppText(text = label, modifier = Modifier.weight(1f), textType = TextType.BodyLarge,
                color = if (enabled) LiloExtendedTheme.colors.textPrimary else LiloExtendedTheme.colors.textDisabled)
            if (selected) Icon(LiloIcons.Check, null, Modifier.size(LiloSize.SmallIcon), tint = LiloExtendedTheme.colors.title)
        }
    }
}
@AppPreviews
@Composable
private fun GroupSelectionRowPreview() {
    LiloPreviewWrapper {
        Column(verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
            GroupSelectionRow("Personal", true, LiloExtendedTheme.colors.taskColor) {}
            GroupSelectionRow("Work", false, LiloExtendedTheme.colors.taskColor) {}
        }
    }
}
