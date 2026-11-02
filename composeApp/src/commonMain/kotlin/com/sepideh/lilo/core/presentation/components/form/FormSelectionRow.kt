package com.sepideh.lilo.core.presentation.components.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FormSelectionRow(icon: ImageVector, title: String, value: String, onClick: () -> Unit,
    modifier: Modifier = Modifier, enabled: Boolean = true, accent: Color? = null,
    onClear: (() -> Unit)? = null, clearContentDescription: String? = null,
    valueBelowTitle: Boolean = false) {
    val colors = LiloExtendedTheme.colors
    Surface(onClick = onClick, modifier = modifier.fillMaxWidth(), enabled = enabled,
        shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp) {
        BoxWithConstraints {
            val valueWidth = maxWidth * .42f
            val stacked = valueBelowTitle || maxWidth < 330.dp || LocalDensity.current.fontScale > 1.2f
            Row(Modifier.fillMaxWidth().heightIn(min = 72.dp).padding(start = 16.dp, end = LiloSpacing.Small, top = LiloSpacing.Item, bottom = LiloSpacing.Item),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
                Surface(shape = CircleShape, color = (accent ?: MaterialTheme.colorScheme.primary).copy(alpha = .14f)) {
                    Box(Modifier.size(LiloSize.IconBadge), contentAlignment = Alignment.Center) {
                        Icon(icon, null, Modifier.size(LiloSize.Icon), tint = if (enabled) colors.title else colors.textDisabled)
                    }
                }
                if (value.isBlank()) {
                    AppText(text = title, modifier = Modifier.weight(1f), textType = TextType.BodyLarge,
                        color = if (enabled) colors.textPrimary else colors.textDisabled, maxLines = 2)
                } else if (stacked) Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Tiny)) {
                    AppText(text = title, textType = TextType.BodyLarge, color = if (enabled) colors.textSecondary else colors.textDisabled)
                    AppText(text = value, textType = TextType.Body, color = if (enabled) colors.textPrimary else colors.textDisabled, maxLines = 2)
                } else {
                    AppText(text = title, modifier = Modifier.weight(1f), textType = TextType.BodyLarge, color = if (enabled) colors.textSecondary else colors.textDisabled)
                    AppText(text = value, modifier = Modifier.widthIn(max = valueWidth).padding(end = LiloSpacing.Small),
                        textType = TextType.Body, color = if (enabled) colors.textPrimary else colors.textDisabled, maxLines = 2)
                }
                if (onClear != null) IconButton(onClick = onClear, enabled = enabled) {
                    Icon(LiloIcons.Close, clearContentDescription, Modifier.size(LiloSize.SmallIcon), tint = if (enabled) colors.textSupporting else colors.textDisabled)
                }
            }
        }
    }
}
@AppPreviews
@Composable
private fun FormSelectionRowPreview() {
    LiloPreviewWrapper { FormSelectionRow(LiloIcons.Groups, "Group", "Personal", {}, accent = LiloExtendedTheme.colors.taskColor) }
}

@AppPreviews
@Composable
private fun ReminderSelectionRowPreview() {
    LiloPreviewWrapper {
        val dateAndTime = if (LocalLayoutDirection.current == LayoutDirection.Rtl) {
            "۱۰ آبان، \u2066۱۱:۴۵\u2069"
        } else {
            "2026-11-01, 11:45"
        }
        Column(Modifier.width(360.dp), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
            FormSelectionRow(
                icon = LiloIcons.Reminder,
                title = stringResource(Res.string.reminder_label),
                value = "$dateAndTime · ${stringResource(Res.string.repeat_daily)}",
                onClick = {},
                accent = LiloExtendedTheme.colors.taskColor,
                onClear = {},
                clearContentDescription = stringResource(Res.string.remove_reminder_action),
                valueBelowTitle = true,
            )
            FormSelectionRow(
                icon = LiloIcons.Reminder,
                title = stringResource(Res.string.reminder_label),
                value = stringResource(Res.string.not_set_label),
                onClick = {},
                accent = LiloExtendedTheme.colors.taskColor,
                valueBelowTitle = true,
            )
        }
    }
}
