package com.sepideh.lilo.settings.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.ui.theme.LiloExtendedTheme

@Composable
fun <T> SettingsChoices(
    options: List<T>,
    selected: T,
    enabled: Boolean,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    icon: (@Composable (T) -> ImageVector)? = null,
    selectionFailed: Boolean = false,
) {
    if (options.isEmpty()) return
    val colors = LiloExtendedTheme.colors
    val selectedIndex = options.indexOf(selected).coerceAtLeast(0)
    val indicator = remember { Animatable(selectedIndex.toFloat()) }
    val scope = rememberCoroutineScope()
    val currentOnSelect by rememberUpdatedState(onSelect)
    val currentSelectedIndex by rememberUpdatedState(selectedIndex)
    val currentSelectionFailed by rememberUpdatedState(selectionFailed)
    var previewIndex by remember { mutableStateOf<Int?>(null) }
    var animating by remember { mutableStateOf(false) }
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    LaunchedEffect(selectedIndex, selectionFailed) {
        // Keep the clicked option highlighted while its preference is being saved.
        if (previewIndex != null && previewIndex != selectedIndex && !selectionFailed) return@LaunchedEffect
        previewIndex = null
        indicator.animateTo(selectedIndex.toFloat(), tween(180))
    }
    Row(
        Modifier.fillMaxWidth().height(IntrinsicSize.Min)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(2.dp).selectableGroup().drawBehind {
                val width = size.width / options.size
                val index = if (rtl) options.lastIndex - indicator.value else indicator.value
                val stroke = 1.dp.toPx()
                val topLeft = Offset(index * width + stroke / 2, stroke / 2)
                val bounds = Size((width - stroke).coerceAtLeast(0f), (size.height - stroke).coerceAtLeast(0f))
                val radius = CornerRadius(16.dp.toPx())
                drawRoundRect(colors.brand.copy(alpha = .08f), topLeft, bounds, radius)
                drawRoundRect(colors.brand.copy(alpha = .45f), topLeft, bounds, radius, style = Stroke(stroke))
            },
    ) {
        options.forEachIndexed { index, value ->
            SettingsChoice(
                text = label(value),
                icon = icon?.invoke(value),
                selected = index == (previewIndex ?: selectedIndex),
                enabled = enabled,
                onClick = {
                    if (!animating && previewIndex == null && index != selectedIndex) scope.launch {
                        animating = true
                        previewIndex = index
                        try {
                            indicator.animateTo(index.toFloat(), tween(180))
                            // Finish before a language change rebuilds the localized UI.
                            currentOnSelect(value)
                            // A repeated failure can retain the same error state without another emission.
                            if (currentSelectionFailed && currentSelectedIndex != index) {
                                previewIndex = null
                                indicator.animateTo(currentSelectedIndex.toFloat(), tween(180))
                            }
                        } finally { animating = false }
                    }
                },
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
