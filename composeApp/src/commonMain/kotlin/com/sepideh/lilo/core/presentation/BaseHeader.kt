package com.sepideh.lilo.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppText
import org.jetbrains.compose.resources.StringResource

@Composable
fun BaseHeader(modifier: Modifier =  Modifier.fillMaxWidth(),title: StringResource) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AppText(
            modifier = Modifier.statusBarsPadding().padding(18.dp),
            text = title, textType = TextType.Title, color = MaterialTheme.colorScheme.onPrimary
        )
    }
}