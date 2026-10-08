package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.ui.theme.*

/** Selection geometry shared by group tabs, filters and repeat options. */
@Composable
fun LiloSelectionChip(label: String, selected: Boolean, accent: Color, onClick: () -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true) {
    FilterChip(selected = selected, onClick = onClick, enabled = enabled,
        modifier = modifier.heightIn(min = LiloSize.TouchTarget), shape = MaterialTheme.shapes.small,
        border = BorderStroke(androidx.compose.ui.unit.Dp(1f), if (selected) accent else MaterialTheme.colorScheme.outlineVariant),
        label = { AppText(text = label, textType = TextType.Body, modifier = Modifier.padding(horizontal = LiloSpacing.Small),
            color = if (!enabled) LiloExtendedTheme.colors.textDisabled else if (selected && accent == LiloExtendedTheme.colors.taskColor) LiloExtendedTheme.colors.taskAction else if (selected) LiloExtendedTheme.colors.title else LiloExtendedTheme.colors.textSecondary) },
        colors = FilterChipDefaults.filterChipColors(containerColor = MaterialTheme.colorScheme.background,
            selectedContainerColor = accent.copy(alpha = .12f)))
}
