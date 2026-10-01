package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.ui.theme.LiloExtendedTheme

@Composable
fun FeatureEmptyIcon(
    feature: LiloFeature,
    modifier: Modifier = Modifier
) {
    val colors = LiloExtendedTheme.colors

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val circleSize = maxWidth * .4f

        Box(
            modifier = Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(
                    feature.accentColor(colors)
                        .copy(alpha = 0.12f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(circleSize * 0.35f),
                imageVector = feature.iconRes,
                tint = feature.accentColor(colors),
                contentDescription = null
            )
        }
    }
}


@AppPreview
@Composable
private fun FeatureEmptyIconPreview() {
    LiloPreviewWrapper{
        FeatureEmptyIcon(
            feature = LiloFeature.TASKS,
        )
    }
}
