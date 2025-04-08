package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource

@Composable
fun AppRowButtons(
    firstButtonTitle: StringResource,
    onFirstButtonClick: () -> Unit,
    secondButtonTitle: StringResource,
    onSecondButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        AppButton(
            modifier = Modifier.weight(1f).padding(vertical = 4.dp, horizontal = 8.dp),
            text = firstButtonTitle,
            onClick = onFirstButtonClick
        )
        AppButton(
            modifier = Modifier.weight(1f).padding(vertical = 4.dp, horizontal = 8.dp),
            text = secondButtonTitle,
            onClick = onSecondButtonClick
        )
    }
}