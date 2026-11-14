package com.sepideh.lilo.core.presentation

import com.sepideh.lilo.ui.theme.*

import com.sepideh.lilo.core.presentation.icons.LiloIcons

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.form.BottomActionFormScreen
import com.sepideh.lilo.core.presentation.components.header.FeatureListHeader
import com.sepideh.lilo.core.presentation.components.AppText
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeatureActionButton(label: StringResource, accent: Color, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = com.sepideh.lilo.ui.theme.LiloExtendedTheme.colors.onAccent),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)) {
        AppText(text = label, textType = TextType.Action)
    }
}

@Composable
fun BaseFormScreen(
    title: StringResource, featureColors: FeatureColors, saveEnabled: Boolean,
    onBack: () -> Boolean, onSave: () -> Unit,
    actionLabel: StringResource = Res.string.save_task_action,
    textSaveAction: Boolean = false,
    saveActionColor: Color = featureColors.primary,
    bottomSaveAction: Boolean = true,
    isSaving: Boolean = false,
    headerColors: FeatureColors? = featureColors,
    content: @Composable (PaddingValues) -> Unit,
) {
    if (bottomSaveAction) {
        BottomActionFormScreen(title, featureColors, saveEnabled, isSaving, actionLabel, onBack, onSave, headerColors, content)
        return
    }
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        BaseHeader(title = title, featureColors = headerColors, modifier = Modifier.statusBarsPadding(), onBackPressed = onBack,
            actions = { if (textSaveAction) TextButton(onClick = onSave, enabled = saveEnabled, colors = ButtonDefaults.textButtonColors(contentColor = saveActionColor)) { Text(text = stringResource(actionLabel), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)) } else FeatureActionButton(actionLabel, featureColors.accent, saveEnabled, onSave) })
    }, content = content)
}

@Composable
fun BaseListScreen(
    title: StringResource, featureColors: FeatureColors, searchVisible: Boolean, query: String,
    searchHint: StringResource, filtersActive: Boolean,
    onBack: () -> Boolean, onSearchVisible: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit, onFilter: (() -> Unit)?,
    floatingActionButton: @Composable () -> Unit = {},
    filterIcon: ImageVector = LiloIcons.Filter,
    filterLabel: StringResource = Res.string.filter_label,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                FeatureListHeader(title, featureColors, searchVisible, query, searchHint, filtersActive,
                    onBack, onSearchVisible, onQueryChange, onFilter, filterIcon, filterLabel)
                Spacer(Modifier.height(LiloSpacing.Screen))
            }
        },
        floatingActionButton = floatingActionButton, content = content)
}

@Composable
fun SheetHeader(title: StringResource, action: StringResource, accent: Color, enabled: Boolean = true, onConfirm: () -> Unit, onReset: (() -> Unit)? = null, resetActionColor: Color = MaterialTheme.colorScheme.onSurfaceVariant, onDismiss: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        if (onDismiss != null) {
            IconButton(onClick = onDismiss) {
                Icon(LiloIcons.Close, stringResource(Res.string.close_sheet_action),
                    Modifier.size(LiloSize.Icon), tint = LiloExtendedTheme.colors.title)
            }
        }
        AppText(text = title, textType = TextType.SectionTitle, modifier = Modifier.weight(1f).padding(end = 12.dp))
        if (onReset != null) {
            TextButton(onClick = onReset, colors = ButtonDefaults.textButtonColors(contentColor = resetActionColor)) {
                Icon(Icons.Rounded.RestartAlt, null, Modifier.size(16.dp))
                Spacer(Modifier.width(LiloSpacing.Tiny))
                AppText(text = Res.string.reset_label, textType = TextType.Action)
            }
        }
        FeatureActionButton(action, accent, enabled, onConfirm)
    }
}
