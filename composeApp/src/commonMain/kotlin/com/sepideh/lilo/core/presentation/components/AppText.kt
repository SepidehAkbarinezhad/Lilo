package com.sepideh.lilo.core.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.styleText
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: StringResource,
    textType: TextType = TextType.Body,
    color: Color = Color.Black,
    textAlign: TextAlign = TextAlign.Justify,
    textDirection: TextDirection = TextDirection.Unspecified,
    textDecoration: TextDecoration = TextDecoration.None,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Text(
        modifier = modifier,
        text = stringResource(text),
        style = styleText(textType).copy(
            textDirection = textDirection,
            textDecoration = textDecoration
        ),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        minLines = minLines,
    )
}

@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: String,
    textType: TextType = TextType.Body,
    color: Color = LocalLiloColorsPalette.current.appText,
    textAlign: TextAlign = TextAlign.Justify,
    textDirection: TextDirection = TextDirection.Unspecified,
    textDecoration: TextDecoration = TextDecoration.None,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Text(
        modifier = modifier,
        text = text,
        style = styleText(textType).copy(
            textDirection = textDirection,
            textDecoration = textDecoration
        ),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        minLines = minLines,
        overflow = TextOverflow.Ellipsis
    )
}





