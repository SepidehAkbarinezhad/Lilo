package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.app_name
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeHeader(
    modifier: Modifier = Modifier.fillMaxWidth(),
    ) {

    Row (modifier = modifier.fillMaxWidth().padding(  18.dp)) {
        AppText(
            modifier = Modifier,
            text =
                Res.string.app_name,
            textType = TextType.Title,
            color = MaterialTheme.colorScheme.onPrimary
        )

    }
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
fun BaseHeaderPreview(modifier: Modifier = Modifier) {
    HomeHeader()
}