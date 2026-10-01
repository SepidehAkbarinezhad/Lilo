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
        TextType.ScreenTitle -> MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold)
        TextType.SectionTitle -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
        TextType.Action -> MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
        TextType.FieldLabel -> MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        TextType.BodyLarge -> MaterialTheme.typography.bodyLarge
        TextType.Caption -> MaterialTheme.typography.bodySmall
        is TextType.Title -> MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium)
        is TextType.SubTitle -> MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium)
        is TextType.Body -> MaterialTheme.typography.bodyMedium
        is TextType.FieldError -> MaterialTheme.typography.bodySmall
    }
}