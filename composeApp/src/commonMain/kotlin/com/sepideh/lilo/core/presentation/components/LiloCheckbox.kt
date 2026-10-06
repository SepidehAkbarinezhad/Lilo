package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.*

/** Small visual checkbox inside a full touch target; one accessible toggleable node. */
@Composable
fun LiloCheckbox(checked: Boolean, enabled: Boolean, label: String, accent: Color, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.size(LiloSize.TouchTarget).toggleable(value = checked, enabled = enabled, role = Role.Checkbox, onValueChange = onCheckedChange)
        .semantics { contentDescription = label }, contentAlignment = Alignment.Center) {
        Surface(Modifier.size(26.dp), shape = MaterialTheme.shapes.extraSmall,
            color = if (checked) accent.copy(alpha = .2f) else Color.Transparent,
            border = if (checked) null else BorderStroke(1.4.dp, if (enabled) LiloExtendedTheme.colors.textSecondary else LiloExtendedTheme.colors.textDisabled)) {
            Box(contentAlignment = Alignment.Center) { if (checked) Icon(LiloIcons.Check, null, Modifier.size(22.dp), tint = LiloExtendedTheme.colors.title) }
        }
    }
}
