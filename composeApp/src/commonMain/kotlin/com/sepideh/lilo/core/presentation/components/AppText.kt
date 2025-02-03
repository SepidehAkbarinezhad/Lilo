package com.sepideh.lilo.core.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun AppText(
    text: String,
    textType: TextType,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        textDecoration = textDecoration,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        minLines = minLines,
        style = textType.style()
    )
}

sealed interface TextType {
    data object Title : TextType
    data object SubTitle : TextType
    data object Body : TextType
}

@Composable
fun TextType.style(): TextStyle {
    return when (this) {
        TextType.Title -> MaterialTheme.typography.titleMedium
        TextType.SubTitle -> MaterialTheme.typography.titleSmall
        TextType.Body -> MaterialTheme.typography.bodyMedium
    }
}




