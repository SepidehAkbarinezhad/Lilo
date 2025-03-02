package com.sepideh.lilo.previews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
fun AppTextPreview(modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
        AppText(text = "title", textType = TextType.Title)
        AppText(text = "subtitle", textType = TextType.SubTitle)
        AppText(text = "body", textType = TextType.Body)
    }
}