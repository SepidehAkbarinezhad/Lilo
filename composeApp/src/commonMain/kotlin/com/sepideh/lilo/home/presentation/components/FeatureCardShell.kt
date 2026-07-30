package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.ui.theme.LiloColors
import com.sepideh.lilo.ui.theme.LocalLiloColorsPalette
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeatureCardShell(
    feature: LiloFeature,
    onAddClick: () -> Unit,
    onMoreClick: () -> Unit,
    onCardClick: () -> Unit,
    detailContent: @Composable () -> Unit
) {
    val colors: LiloColors = LocalLiloColorsPalette.current
    with(feature) {
        Card(
            modifier = Modifier.padding(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = accentColor(colors).copy(alpha = 0.12f)
            )
        ) {

            Row(
                Modifier.fillMaxWidth().padding(12.dp),


                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = feature.accentColor(colors)
                    )
                ) {
                    Icon(
                        modifier = Modifier.padding(12.dp).size(24.dp),
                        imageVector = feature.iconRes,
                        tint = Color.White,
                        contentDescription = ""
                    )

                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    AppText(text = stringResource(feature.titleRes), textType = TextType.Title)

                    AppText(text = stringResource(feature.titleRes), textType = TextType.SubTitle)
                }
                IconButton(onClick = onAddClick) { }
                IconButton(onClick = onMoreClick) { }
            }
            detailContent()
        }

    }
}

