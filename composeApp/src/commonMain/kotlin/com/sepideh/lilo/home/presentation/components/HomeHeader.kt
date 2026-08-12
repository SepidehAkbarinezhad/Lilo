package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppHeader
import com.sepideh.lilo.core.presentation.components.AppPreview
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.app_name
import lilo.composeapp.generated.resources.ic_settings
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeHeader(
    onAction: (BaseAction) -> Unit
) {
    AppHeader {
        AppText(
            modifier = Modifier,
            text =
                Res.string.app_name,
            textType = TextType.Title,
            color = MaterialTheme.colorScheme.onPrimary
        )
        SettingButton { onAction(BaseAction.OnNavigateTo(AppRoutes.Settings)) }
    }
}

@Composable
fun SettingButton(onSettingClicked: () -> Unit) {
    IconButton(
        onClick = onSettingClicked,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_settings),
            contentDescription = "Open setting"
        )
    }
}
@AppPreview
@Composable
fun BaseHeaderPreview() {
    LiloPreviewWrapper {
        HomeHeader(onAction = {})
    }
}