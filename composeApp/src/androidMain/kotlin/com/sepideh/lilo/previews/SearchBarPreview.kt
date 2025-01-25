package com.sepideh.lilo.previews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sepideh.lilo.core.presentation.components.AppSearchBar
import androidx.compose.ui.tooling.preview.Preview



@Preview
@Composable
fun SearchBarPreview() {
    Box(
        Modifier
            .fillMaxWidth()
            .background(Color.White)){
        AppSearchBar(
            modifier = Modifier.fillMaxWidth(),
            searchQuery = "",
            onImeSearch = {}
        ) { }
    }
}

