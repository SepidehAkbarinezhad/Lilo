package com.sepideh.lilo.core.presentation.components.header

import com.sepideh.lilo.ui.theme.featureTextSelectionColors
import com.sepideh.lilo.ui.theme.FeatureColors
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppPreviews
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSize
import com.sepideh.lilo.ui.theme.LiloSpacing
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeatureListHeader(
    title: StringResource,
    featureColors: FeatureColors,
    searchVisible: Boolean,
    query: String,
    searchHint: StringResource,
    filtersActive: Boolean,
    onBack: () -> Boolean,
    onSearchVisible: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onFilter: (() -> Unit)?,
    filterIcon: ImageVector = LiloIcons.Filter,
    filterLabel: StringResource = Res.string.filter_label,
) {
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val actionSurface = featureColors.headerControlContainer
    val headerContentColor = LiloExtendedTheme.colors.title

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp),
        color = featureColors.primaryContainer,
        contentColor = headerContentColor,
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                .heightIn(min = 64.dp)
                .padding(horizontal = LiloSpacing.Item, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Small),
        ) {
            if (searchVisible) {
                val focus = remember { FocusRequester() }
                LaunchedEffect(Unit) { focus.requestFocus() }
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f).focusRequester(focus)
                        .border(1.dp, headerContentColor.copy(alpha = .18f), CircleShape),
                    singleLine = true,
                    shape = CircleShape,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    placeholder = {
                        AppText(text = searchHint, color = headerContentColor, maxLines = 1)
                    },
                    trailingIcon = {
                        IconButton(onClick = {
                            focusManager.clearFocus()
                            keyboard?.hide()
                            onSearchVisible(false)
                            onQueryChange("")
                        }) {
                            Icon(LiloIcons.Close, stringResource(Res.string.close_search_action), tint = headerContentColor)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(selectionColors = featureTextSelectionColors(featureColors.accent),
                        focusedContainerColor = actionSurface,
                        unfocusedContainerColor = actionSurface,
                        focusedTextColor = headerContentColor,
                        unfocusedTextColor = headerContentColor,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = headerContentColor,
                    ),
                )
            } else {
                IconButton(onClick = { onBack() }) {
                    Icon(LiloIcons.Back, stringResource(Res.string.back_action), Modifier.size(LiloSize.Icon))
                }
                AppText(
                    text = title,
                    textType = TextType.ScreenTitle,
                    modifier = Modifier.weight(1f),
                    color = headerContentColor,
                    maxLines = 1,
                )
                Surface(onClick = { onSearchVisible(true) }, shape = CircleShape, color = actionSurface, contentColor = headerContentColor) {
                    Box(Modifier.size(LiloSize.TouchTarget), contentAlignment = Alignment.Center) {
                        Icon(LiloIcons.Search, stringResource(searchHint), Modifier.size(LiloSize.Icon))
                    }
                }
            }
            if (onFilter != null) {
                Surface(
                    onClick = {
                        focusManager.clearFocus()
                        keyboard?.hide()
                        onSearchVisible(false)
                        onFilter()
                    },
                    modifier = Modifier.semantics { selected = filtersActive },
                    shape = CircleShape,
                    color = if (filtersActive) featureColors.primary else actionSurface,
                    contentColor = if (filtersActive) featureColors.onPrimary else headerContentColor,
                ) {
                    Box(Modifier.size(LiloSize.TouchTarget), contentAlignment = Alignment.Center) {
                        Icon(filterIcon, stringResource(filterLabel), Modifier.size(LiloSize.Icon))
                    }
                }
            }
        }
    }
}

@AppPreviews
@Composable
private fun FeatureListHeaderPreview() {
    LiloPreviewWrapper {
        Column(verticalArrangement = Arrangement.spacedBy(LiloSpacing.Section)) {
            listOf(false to false, false to true, true to true).forEach { (searchVisible, filtersActive) ->
                FeatureListHeader(
                    title = Res.string.tasks_list_title, featureColors = LiloExtendedTheme.colors.tasks,
                    searchVisible = searchVisible, query = "", searchHint = Res.string.search_tasks_action,
                    filtersActive = filtersActive, onBack = { true }, onSearchVisible = {}, onQueryChange = {}, onFilter = {},
                )
            }
        }
    }
}
