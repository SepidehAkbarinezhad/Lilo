package com.sepideh.lilo.core.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppText
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeatureActionButton(label: StringResource, accent: Color, enabled: Boolean = true, onClick: () -> Unit) {
    Button(onClick = onClick, enabled = enabled, shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)) {
        AppText(text = label, textType = TextType.Action)
    }
}

@Composable
fun BaseFormScreen(
    title: StringResource, accent: Color, saveEnabled: Boolean,
    onBack: () -> Boolean, onSave: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background, topBar = {
        BaseHeader(title = title, modifier = Modifier.statusBarsPadding(), onBackPressed = onBack,
            actions = { FeatureActionButton(Res.string.save_task_action, accent, saveEnabled, onSave) })
    }, content = content)
}

@Composable
fun BaseListScreen(
    title: StringResource, accent: Color, searchVisible: Boolean, query: String,
    searchHint: StringResource, filtersActive: Boolean,
    onBack: () -> Boolean, onSearchVisible: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit, onFilter: () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background,
        topBar = { ListHeader(title, accent, searchVisible, query, searchHint, filtersActive, onBack, onSearchVisible, onQueryChange, onFilter) },
        floatingActionButton = floatingActionButton, content = content)
}

@Composable
private fun ListHeader(
    title: StringResource, accent: Color, searchVisible: Boolean, query: String,
    searchHint: StringResource, filtersActive: Boolean, onBack: () -> Boolean,
    onSearchVisible: (Boolean) -> Unit, onQueryChange: (String) -> Unit, onFilter: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    BaseHeader(title = title, modifier = Modifier.statusBarsPadding(), onBackPressed = onBack,
        titleContent = {
            AnimatedContent(targetState = searchVisible, modifier = Modifier.weight(1f), label = "expand-search") { expanded ->
                if (expanded) {
                    val focus = remember { FocusRequester() }
                    LaunchedEffect(Unit) { focus.requestFocus() }
                    OutlinedTextField(value = query, onValueChange = onQueryChange, singleLine = true,
                        modifier = Modifier.fillMaxWidth().focusRequester(focus), shape = RoundedCornerShape(24.dp),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        placeholder = { AppText(text = searchHint, maxLines = 1, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        // Search starts at the end when collapsed; close occupies the opposite edge when expanded.
                        leadingIcon = { IconButton(onClick = { focusManager.clearFocus(); onSearchVisible(false) }) {
                            Icon(Icons.Outlined.Close, stringResource(Res.string.close_search_action), Modifier.size(20.dp))
                        } },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accent,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, cursorColor = accent))
                } else Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    AppText(text = title, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), textType = TextType.ScreenTitle, maxLines = 1)
                    IconButton(onClick = { onSearchVisible(true) }) { Icon(Icons.Outlined.Search, stringResource(searchHint), Modifier.size(22.dp)) }
                }
            }
        }, actions = {
            IconButton(onClick = { focusManager.clearFocus(); onFilter() }) {
                Icon(Icons.Outlined.Tune, stringResource(Res.string.filter_label), Modifier.size(22.dp),
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
