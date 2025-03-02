package com.sepideh.lilo.core.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

sealed interface TextType {
    data object Title : TextType
    data object SubTitle : TextType
    data object Body : TextType
    data object FieldError : TextType
}

@Composable
fun styleText(textType: TextType): TextStyle {
    return when (textType) {
        is TextType.Title -> MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        is TextType.SubTitle -> MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        is TextType.Body -> MaterialTheme.typography.bodyMedium
        is TextType.FieldError -> MaterialTheme.typography.bodySmall
    }
}