package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
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
        shape = RoundedCornerShape(size = 12.dp),
        onClick = onClick,

        ) {
        AppText(
            modifier = Modifier.padding(vertical = 12.dp),
            text = stringResource(resource = text),
            color = Color.White,
            textType = TextType.SubTitle
        )
    }

}