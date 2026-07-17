package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.ui.theme.LiloColors
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FeatureCardShell(
    feature: LiloFeature,
    onAddClick: () -> Unit,
    onMoreClick: () -> Unit,
    onCardClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors : LiloColors = LocalLiloColorsPalette.current
    Card(modifier = Modifier.padding(12.dp)) {
        with(feature) {
            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(colors = CardDefaults.cardColors(containerColor = accentColor(colors))) {
                    Icon(
                        modifier = Modifier.padding(12.dp).size(24.dp),
                        imageVector = feature.iconRes,
                        tint = feature.accentColor(colors),
                        contentDescription = ""
                    )

                }
                AppText(text = stringResource(feature.titleRes))
                IconButton(onClick = onAddClick) { }
                IconButton(onClick = onMoreClick) { }
            }
            content()
        }

    }
}

@Preview
@Composable
fun FeatureCardShellPrev() {
    FeatureCardShell(
        feature = LiloFeature.TASKS,
        onAddClick = {},
        onMoreClick = {},
        onCardClick = {},
        content = {}
    )
}