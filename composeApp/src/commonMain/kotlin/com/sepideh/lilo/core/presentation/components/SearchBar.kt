package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onImeSearch: () -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        shape = RoundedCornerShape(100),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = MaterialTheme.colors.primary,
            focusedLabelColor = Color.Yellow
        ),
        placeholder = { Text(text = stringResource(Res.string.search_hint)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) }
    )
}