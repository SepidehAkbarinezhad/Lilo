package com.sepideh.lilo.core.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

sealed interface TextType {
    data object ScreenTitle : TextType
    data object SectionTitle : TextType
    data object Action : TextType
    data object FieldLabel : TextType
    data object BodyLarge : TextType
    data object Caption : TextType
    data object Title : TextType
    data object SubTitle : TextType
    data object Body : TextType
    data object FieldError : TextType
}

@Composable
fun styleText(textType: TextType): TextStyle {
    return when (textType) {
        TextType.ScreenTitle -> MaterialTheme.typography.titleLarge
        TextType.SectionTitle -> MaterialTheme.typography.titleMedium
        TextType.Action -> MaterialTheme.typography.labelLarge
        TextType.FieldLabel -> MaterialTheme.typography.bodyMedium
        TextType.BodyLarge -> MaterialTheme.typography.bodyLarge
        TextType.Caption -> MaterialTheme.typography.bodySmall
        is TextType.Title -> MaterialTheme.typography.titleMedium
        is TextType.SubTitle -> MaterialTheme.typography.titleSmall
        is TextType.Body -> MaterialTheme.typography.bodyMedium
        is TextType.FieldError -> MaterialTheme.typography.bodySmall
    }
}