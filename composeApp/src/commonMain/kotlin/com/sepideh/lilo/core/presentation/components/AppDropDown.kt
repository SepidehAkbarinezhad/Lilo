package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType

@Composable
fun AppDropDown(
    selectedValue: String,
    options: List<String>,
    label: String,
    onValueChangedEvent: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    var isFocused by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isFocused) {
        println("me LaunchedEffect  isFocused $isFocused")
        expanded = isFocused
        println("me LaunchedEffect expanded  $expanded")
    }
    val focusedColor = if (isFocused) MaterialTheme.colorScheme.primary else Gray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        AppText(
            modifier = Modifier.background(Blue),
            text = "category",
            textType = TextType.SubTitle,
            color = focusedColor,
        )
        Box(
            modifier = Modifier.fillMaxWidth().clickable(indication = null, // Disable the ripple effect
            interactionSource = remember { MutableInteractionSource() } ) { expanded = !expanded }.border(
                width = 1.dp,
                color = focusedColor,
                shape = MaterialTheme.shapes.small
            )
        ) {
            AppText(modifier = Modifier.fillMaxWidth().padding(12.dp), text = "selected")
        }
        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    text = { Text(option) },
                    onClick = { }
                )
            }
        }
    }

}




