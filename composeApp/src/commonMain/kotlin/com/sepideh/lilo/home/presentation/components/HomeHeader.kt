package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.app.navigation.AppRoutes
import com.sepideh.lilo.core.domain.model.AppLanguage
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.brand.LiloWordmark
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.*

@Composable
fun HomeHeader(onAction: (BaseAction) -> Unit) {
    val brand = if (MaterialTheme.colorScheme.background.luminance() < .5f) HomeBrandDark else HomeBrandLight
    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (LocalLiloAppLanguage.current == AppLanguage.FA) {
            Text(
                text = stringResource(Res.string.home_brand_name),
                style = MaterialTheme.typography.headlineLarge,
                color = brand,
                maxLines = 1
            )
        } else {
            LiloWordmark(Modifier.size(width = 60.dp, height = 30.dp), color = brand)
        }
        Spacer(Modifier.weight(1f))
        Surface(shape = RoundedCornerShape(15.dp), color = brand.copy(alpha = .09f)) {
            IconButton(onClick = { onAction(BaseAction.OnNavigateTo(AppRoutes.Settings)) }) {
                Icon(vectorResource(Res.drawable.home_settings), stringResource(Res.string.home_settings_action),
                    Modifier.size(23.dp), tint = brand)
            }
        }
    }
}

@AppPreviews
@Composable
private fun HomeHeaderPreview() { LiloPreviewWrapper { HomeHeader({}) } }
