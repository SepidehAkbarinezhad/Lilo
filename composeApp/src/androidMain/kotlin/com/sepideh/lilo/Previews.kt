package com.sepideh.lilo

import androidx.compose.runtime.Composable
import com.sepideh.lilo.core.presentation.components.SearchBar
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(
        searchQuery = "task",
        onImeSearch = {},
        onSearchQueryChange = {}
    )
}