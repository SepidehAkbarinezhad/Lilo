package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSize
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.description_label
import org.jetbrains.compose.resources.stringResource

/** Three visible lines; the text field scrolls internally as the content grows. */
@Composable
fun AppDescriptionTextField(
    textFieldRequired: TextFieldRequired,
    accentColor: Color,
    modifier: Modifier = Modifier,
) {
    AppOutlineTextField(
        containerModifier = modifier,
        textFieldRequired = textFieldRequired,
        accentColor = accentColor,
        singleLine = false,
        minLines = 3,
        maxLines = 3,
        topAlignedIcon = true,
        leadingIcon = { Icon(LiloIcons.Description, null, Modifier.size(LiloSize.Icon)) },
    )
}

@AppPreviews
@Composable
private fun EmptyDescriptionPreview() {
    LiloPreviewWrapper {
        var value by remember { mutableStateOf("") }
        AppDescriptionTextField(TextFieldRequired(label = stringResource(Res.string.description_label),
            value = value, onValueChange = { value = it }), LiloExtendedTheme.colors.taskColor)
    }
}

@AppPreviews
@Composable
private fun ScrollableDescriptionPreview() {
    LiloPreviewWrapper {
        var value by remember { mutableStateOf("First line\nSecond line\nThird line\nFourth line\nFifth line") }
        AppDescriptionTextField(TextFieldRequired(label = stringResource(Res.string.description_label),
            value = value, onValueChange = { value = it }), LiloExtendedTheme.colors.noteColor)
    }
}
