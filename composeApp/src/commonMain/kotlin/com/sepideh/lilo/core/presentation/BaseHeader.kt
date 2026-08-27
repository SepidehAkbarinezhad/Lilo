package com.sepideh.lilo.core.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppText
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.tasks_list_title
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BaseHeader(
    modifier: Modifier = Modifier.fillMaxWidth(),
    title: StringResource,
    mainScreen : Boolean = false,
    onBackPressed: () -> Boolean = {true}
) {

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Box(modifier = modifier.padding(vertical = 18.dp)) {

        AppText(
            modifier = Modifier.statusBarsPadding().align(Alignment.Center),
            text = title, textType = TextType.Title, color = MaterialTheme.colorScheme.onPrimary
        )
        if(!mainScreen){
            IconButton(
                modifier = Modifier.statusBarsPadding().align(Alignment.CenterStart),
                onClick = {
                    onBackPressed()
                },
            ) {
                Icon(
                    imageVector = if(isRtl) Icons.Default.ArrowForwardIos else Icons.Default.ArrowBackIosNew,
                    contentDescription = "",
                    tint = White
                )
            }
        }

    }
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Composable
fun BaseHeaderPreview(modifier: Modifier = Modifier) {
    BaseHeader(title = Res.string.tasks_list_title, onBackPressed = {true})
}