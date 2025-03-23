package com.sepideh.lilo.core.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.styleText
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: StringResource,
    textType: TextType = TextType.Body,
    color: Color = Color.DarkGray,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        modifier = modifier,
        text = stringResource(text),
        style = styleText(textType),
        color = color,
        textAlign = textAlign
    )
}

@Composable
fun AppText(
    modifier: Modifier = Modifier,
    text: String,
    textType: TextType = TextType.Body,
    color: Color = Color.Black,
    textAlign: TextAlign = TextAlign.Start,
    textDirection: TextDirection = TextDirection.Rtl,
    textDecoration: TextDecoration=TextDecoration.None
) {
    Text(
        modifier = modifier,
        text = text,
        style = styleText(textType).copy(textDirection = textDirection, textDecoration = textDecoration),
        color = color,
        textAlign = textAlign
    )
}





