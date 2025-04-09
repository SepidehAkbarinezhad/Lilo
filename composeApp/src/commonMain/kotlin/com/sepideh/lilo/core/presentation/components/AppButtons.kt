package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppButton(
    text: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(size = 8.dp),
        onClick = onClick,
        ) {
        AppText(
            text = stringResource(resource = text),
            color = Color.White,
            textType = TextType.SubTitle
        )
    }
}

@Composable
fun AppSingleButton(
    text: StringResource,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.fillMaxWidth().padding(24.dp),
        shape = RoundedCornerShape(size = 8.dp),
        onClick = onClick,
    ) {
        AppText(
            modifier = Modifier.padding(8.dp),
            text = stringResource(resource = text),
            color = Color.White,
            textType = TextType.SubTitle
        )
    }
}



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
            modifier = Modifier.weight(1f).padding( horizontal = 8.dp),
            text = firstButtonTitle,
            onClick = onFirstButtonClick
        )
        AppButton(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            text = secondButtonTitle,
            onClick = onSecondButtonClick
        )
    }
}