package com.sepideh.lilo.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.format.creationDateLabel
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreationDateCaption(timestamp: Long, modifier: Modifier = Modifier, showLabel: Boolean = true) {
    val date = creationDateLabel(timestamp, LocalLayoutDirection.current == LayoutDirection.Rtl) ?: return
    AppText(text = if (showLabel) stringResource(Res.string.created_on_date, date) else date, textType = TextType.Caption,
        color = LiloExtendedTheme.colors.textSupporting, modifier = modifier)
}

@AppPreviews
@Composable
private fun CreationDateCaptionPreview() {
    LiloPreviewWrapper { CreationDateCaption(1767225600000L) }
}
