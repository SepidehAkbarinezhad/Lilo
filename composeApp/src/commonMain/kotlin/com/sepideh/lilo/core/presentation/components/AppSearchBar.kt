package com.sepideh.lilo.core.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.search_hint
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppSearchBar(
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    searchQuery: String,
    onImeSearch: () -> Unit,
    readonly: Boolean,
    onSearchQueryChange: (String) -> Unit
) {

    LaunchedEffect(Unit){
        focusRequester.captureFocus()
    }

        OutlinedTextField(
            modifier = modifier.background(
                shape = RoundedCornerShape(100),
                color = Color.White
            ).focusRequester(focusRequester),
            shape = RoundedCornerShape(100),
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            textStyle = TextStyle(color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.secondary,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            placeholder = {
                AppText(
                    text = stringResource(
                        Res.string.search_hint
                    )
                )
            },
            leadingIcon = {
                IconButton(onClick = {
                   onImeSearch()
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = if(searchQuery.isNotBlank()) MaterialTheme.colorScheme.secondary.copy(alpha = .7f) else MaterialTheme.colorScheme.onSurface
                    )
                }

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
                            tint = MaterialTheme.colorScheme.secondary.copy(alpha = .7f)
                        )
                    }
                }
            },
            readOnly = readonly

        )
    }


