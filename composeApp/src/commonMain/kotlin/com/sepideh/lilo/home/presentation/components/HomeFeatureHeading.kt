package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun HomeFeatureHeading(feature: LiloFeature, actionColor: Color, onAdd: () -> Unit, onList: () -> Unit) {
    val colors = LiloExtendedTheme.colors
    val title = stringResource(feature.titleRes)
    val slogan = when (feature) {
        LiloFeature.TASKS -> Res.string.home_task_slogan
        LiloFeature.NOTES -> Res.string.home_note_slogan
        LiloFeature.PASSWORDS -> Res.string.home_password_slogan
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(shape = RoundedCornerShape(16.dp), color = feature.accentColor(colors).copy(alpha = .32f)) {
            Box(Modifier.size(52.dp), contentAlignment = Alignment.Center) {
                Icon(if (feature == LiloFeature.NOTES) vectorResource(Res.drawable.home_note) else feature.iconRes,
                    null, Modifier.size(28.dp), tint = actionColor)
            }
        }
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                AppText(text = title, textType = TextType.SectionTitle, modifier = Modifier.weight(1f), maxLines = 2)
                // Bring the glyphs closer while retaining separate 48dp touch targets.
                IconButton(onClick = onAdd, modifier = Modifier.size(48.dp)) {
                    Icon(LiloIcons.Add, stringResource(Res.string.home_add_feature, title),
                        Modifier.size(24.dp).offset(x = 4.dp), tint = actionColor)
                }
                IconButton(onClick = onList, modifier = Modifier.size(48.dp)) {
                    Icon(vectorResource(Res.drawable.home_list), stringResource(Res.string.home_open_list, title),
                        Modifier.size(24.dp).offset(x = (-4).dp), tint = actionColor)
                }
            }
            AppText(text = slogan, textType = TextType.Caption, color = colors.textSecondary)
        }
    }
}
