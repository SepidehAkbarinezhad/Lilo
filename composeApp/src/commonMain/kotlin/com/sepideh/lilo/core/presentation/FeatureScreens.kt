package com.sepideh.lilo.core.presentation

import com.sepideh.lilo.ui.theme.*

import com.sepideh.lilo.core.presentation.icons.LiloIcons

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
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
    title: StringResource, accent: Color, saveEnabled: Boolean,
    onBack: () -> Boolean, onSave: () -> Unit,
    actionLabel: StringResource = Res.string.save_task_action,
    textSaveAction: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        BaseHeader(title = title, modifier = Modifier.statusBarsPadding(), onBackPressed = onBack,
            actions = { if (textSaveAction) TextButton(onClick = onSave, enabled = saveEnabled, colors = ButtonDefaults.textButtonColors(contentColor = if (accent == LiloExtendedTheme.colors.taskColor) LiloExtendedTheme.colors.taskAction else accent)) { AppText(text = actionLabel, textType = TextType.Action) } else FeatureActionButton(actionLabel, accent, saveEnabled, onSave) })
    }, content = content)
}

@Composable
fun BaseListScreen(
    title: StringResource, accent: Color, searchVisible: Boolean, query: String,
    searchHint: StringResource, filtersActive: Boolean,
    onBack: () -> Boolean, onSearchVisible: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit, onFilter: () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    referenceStyle: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        topBar = { ListHeader(title, accent, searchVisible, query, searchHint, filtersActive, onBack, onSearchVisible, onQueryChange, onFilter, referenceStyle) },
        floatingActionButton = floatingActionButton, content = content)
}

@Composable
private fun ListHeader(
    title: StringResource, accent: Color, searchVisible: Boolean, query: String,
    searchHint: StringResource, filtersActive: Boolean, onBack: () -> Boolean,
    onSearchVisible: (Boolean) -> Unit, onQueryChange: (String) -> Unit, onFilter: () -> Unit,
    referenceStyle: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    if (referenceStyle) {
        Row(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 20.dp, vertical = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
            if (searchVisible) {
                val focus = remember { FocusRequester() }
                LaunchedEffect(Unit) { focus.requestFocus() }
                OutlinedTextField(value = query, onValueChange = onQueryChange, singleLine = true,
                    modifier = Modifier.weight(1f).focusRequester(focus), shape = CircleShape,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    placeholder = { AppText(text = searchHint, color = LiloExtendedTheme.colors.textPlaceholder) },
                    trailingIcon = { IconButton(onClick = { focusManager.clearFocus(); keyboard?.hide(); onSearchVisible(false); onQueryChange("") }) {
                        Icon(LiloIcons.Close, stringResource(Res.string.close_search_action))
                    } }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent, unfocusedBorderColor = accent, cursorColor = accent))
            } else {
                IconButton(onClick = { onBack() }) { Icon(LiloIcons.Back, stringResource(Res.string.back_action), tint = LiloExtendedTheme.colors.title) }
                AppText(text = title, textType = TextType.ScreenTitle, modifier = Modifier.weight(1f), maxLines = 1)
                Surface(onClick = { onSearchVisible(true) }, shape = CircleShape, border = BorderStroke(1.dp, accent), color = MaterialTheme.colorScheme.background) {
                    Box(Modifier.size(LiloSize.TouchTarget), contentAlignment = Alignment.Center) { Icon(LiloIcons.Search, stringResource(searchHint), Modifier.size(LiloSize.Icon), tint = LiloExtendedTheme.colors.title) }
                }
            }
            Surface(onClick = { focusManager.clearFocus(); keyboard?.hide(); onSearchVisible(false); onFilter() }, shape = CircleShape,
                border = BorderStroke(1.dp, accent), color = if (filtersActive) accent.copy(alpha = .16f) else MaterialTheme.colorScheme.background) {
                Box(Modifier.size(LiloSize.TouchTarget), contentAlignment = Alignment.Center) { Icon(LiloIcons.Filter, stringResource(Res.string.filter_label), Modifier.size(LiloSize.Icon), tint = LiloExtendedTheme.colors.title) }
            }
        }
        return
    }
    BaseHeader(title = title, modifier = Modifier.statusBarsPadding(), onBackPressed = onBack,
        titleContent = {
            AnimatedContent(targetState = searchVisible, modifier = Modifier.weight(1f), label = "expand-search") { expanded ->
                if (expanded) {
                    val focus = remember { FocusRequester() }
                    LaunchedEffect(Unit) { focus.requestFocus() }
                    OutlinedTextField(value = query, onValueChange = onQueryChange, singleLine = true,
                        modifier = Modifier.fillMaxWidth().focusRequester(focus), shape = MaterialTheme.shapes.extraLarge,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        placeholder = { AppText(text = searchHint, maxLines = 1, color = LiloExtendedTheme.colors.textPlaceholder) },
                        // Search starts at the end when collapsed; close occupies the opposite edge when expanded.
                        leadingIcon = { IconButton(onClick = { focusManager.clearFocus(); keyboard?.hide(); onSearchVisible(false) }) {
                            Icon(LiloIcons.Close, stringResource(Res.string.close_search_action), Modifier.size(LiloSize.SmallIcon))
                        } },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, cursorColor = accent))
                } else Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    AppText(text = title, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), textType = TextType.ScreenTitle, maxLines = 1)
                    IconButton(onClick = { onSearchVisible(true) }) { Icon(LiloIcons.Search, stringResource(searchHint), Modifier.size(22.dp)) }
                }
            }
        }, actions = {
            IconButton(onClick = { focusManager.clearFocus(); keyboard?.hide(); onSearchVisible(false); onFilter() }) {
                Icon(LiloIcons.Filter, stringResource(Res.string.filter_label), Modifier.size(22.dp),
                    tint = if (filtersActive) accent else MaterialTheme.colorScheme.onSurface)
            }
        })
}

@Composable
fun SheetHeader(title: StringResource, action: StringResource, accent: Color, enabled: Boolean = true, onConfirm: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        AppText(text = title, textType = TextType.SectionTitle, modifier = Modifier.weight(1f).padding(end = 12.dp))
        FeatureActionButton(action, accent, enabled, onConfirm)
    }
}
