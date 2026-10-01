package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppHeader
import com.sepideh.lilo.core.presentation.components.AppPreview
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import com.sepideh.lilo.ui.icons.SettingIcon
import lilo.composeapp.generated.resources.Res
import lilo.composeapp.generated.resources.app_name

@Composable
fun HomeHeader(
    onAction: (BaseAction) -> Unit
) {
    AppHeader {
        AppText(
            modifier = Modifier.padding(horizontal = 4.dp),
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


    Icon(
        modifier = Modifier.clickable { onSettingClicked() },
        imageVector = SettingIcon,
        tint = MaterialTheme.colorScheme.onPrimary,
        contentDescription = "Open setting"
    )

}

@AppPreview
@Composable
fun BaseHeaderPreview() {
    LiloPreviewWrapper {
        HomeHeader(onAction = {})
    }
}