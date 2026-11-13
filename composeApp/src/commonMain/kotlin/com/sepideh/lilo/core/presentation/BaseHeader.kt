package com.sepideh.lilo.core.presentation

import com.sepideh.lilo.ui.theme.*

import com.sepideh.lilo.core.presentation.icons.LiloIcons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppText
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.back_action
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Shared geometry. Screen wrappers own system insets; this header owns only its content. */
@Composable
fun BaseHeader(
    modifier: Modifier = Modifier,
    title: StringResource,
    mainScreen: Boolean = false,
    onBackPressed: () -> Boolean = { true },
    actions: @Composable RowScope.() -> Unit = {},
    titleContent: (@Composable RowScope.() -> Unit)? = null,
    featureColors: FeatureColors? = null,
) {
    // Optional feature background; other BaseHeader usages retain their original styling.
    val background = if (featureColors != null) Modifier.background(featureColors.primaryContainer) else Modifier
    Row(background.then(modifier).fillMaxWidth().heightIn(min = 64.dp).padding(horizontal = LiloSpacing.Item), verticalAlignment = Alignment.CenterVertically) {
        if (!mainScreen) IconButton(onClick = { onBackPressed() }) {
            Icon(LiloIcons.Back, stringResource(Res.string.back_action), Modifier.size(LiloSize.Icon), tint = LiloExtendedTheme.colors.title)
        }
        if (titleContent != null) titleContent()
        else if (featureColors != null) AppText(text = title, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), textType = TextType.ScreenTitle, color = LiloExtendedTheme.colors.title, maxLines = 1)
        else AppText(text = title, modifier = Modifier.weight(1f).padding(horizontal = 8.dp), textType = TextType.ScreenTitle, maxLines = 1)
        actions()
    }
}
