package com.sepideh.lilo.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.ui.theme.LiloExtendedTheme

/** Equal-width choices mirror naturally with the app's reading direction. */
@Composable
fun <T> SettingsChoices(
    options: List<T>,
    selected: T,
    enabled: Boolean,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    icon: (@Composable (T) -> ImageVector)? = null,
) {
    Row(
        Modifier.fillMaxWidth().height(IntrinsicSize.Min)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(2.dp).selectableGroup(),
    ) {
        options.forEach { value ->
            SettingsChoice(
                text = label(value),
                icon = icon?.invoke(value),
                selected = value == selected,
                enabled = enabled,
                onClick = { onSelect(value) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
            )
        }
    }
}

@Composable
private fun SettingsChoice(
    text: String,
    icon: ImageVector?,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    val colors = LiloExtendedTheme.colors
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier.heightIn(min = if (icon == null) 54.dp else 76.dp)
            .clip(shape)
            .background(if (selected) colors.brand.copy(alpha = .08f) else Color.Transparent)
            .then(if (selected) Modifier.border(1.dp, colors.brand.copy(alpha = .45f), shape) else Modifier)
            .selectable(selected, enabled = enabled, role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = if (icon == null) 12.dp else 8.dp)
            .alpha(if (enabled) 1f else .5f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(23.dp),
                tint = if (selected) colors.brand else colors.textSecondary)
        }
        AppText(text = text, textAlign = TextAlign.Center,
            color = if (selected) colors.title else colors.textSecondary)
    }
}
