package com.sepideh.lilo.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize


import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppSearchBar(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onImeSearch: () -> Unit,
    readonly : Boolean,
    onSearchQueryChange: (String) -> Unit
) {
    CompositionLocalProvider(
        value = LocalTextSelectionColors provides  TextSelectionColors(
            handleColor = Color.Blue,
            backgroundColor = Color.Blue
        )
    ){
        OutlinedTextField(
            modifier = modifier.background(
                shape = RoundedCornerShape(100),
                color = Color.White
            ),
            shape = RoundedCornerShape(100),
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            colors = OutlinedTextFieldDefaults.colors(cursorColor = MaterialTheme.colorScheme.tertiary, focusedBorderColor = MaterialTheme.colorScheme.tertiary, unfocusedBorderColor = MaterialTheme.colorScheme.secondary),
            placeholder = { Text(text = stringResource(Res.string.search_hint
            )) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = .66f)
                )
            },
            singleLine = true,
            keyboardActions = KeyboardActions(onSearch = { onImeSearch() }),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            trailingIcon = {
                AnimatedVisibility(visible = searchQuery.isNotBlank()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.search_hint),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            readOnly = readonly

        )
    }


}