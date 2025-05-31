package com.sepideh.lilo.core.presentation.components

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
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType

@Composable
fun AppDropDown(
    selectedValue: String,
    options: List<String>,
    label: String,
    onValueChanged: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    var isFocused by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(isFocused) {
        expanded = isFocused
    }
    val focusedColor = if (isFocused) MaterialTheme.colorScheme.primary else Gray

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        AppText(
            text = label,
            textType = TextType.SubTitle,
            color = focusedColor,
        )
        Box(
            modifier = Modifier.fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // Disable the ripple effect
                ) {
                    expanded = !expanded
                }.border(
                    width = 1.dp,
                    color = focusedColor,
                    shape = MaterialTheme.shapes.small
                )
        ) {
            AppText(modifier = Modifier.padding(18.dp), text = selectedValue)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { AppText(text = option) },
                    onClick = {
                        expanded = false
                        onValueChanged(option)
                    }
                )
            }
        }
    }

}




