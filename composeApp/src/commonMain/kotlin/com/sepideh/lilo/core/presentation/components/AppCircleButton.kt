package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.sepideh.lilo.home.presentation.model.LiloFeature
import com.sepideh.lilo.ui.theme.LiloExtendedTheme

@Composable
fun AppCircleButton(imageVector: ImageVector = Icons.Default.Add, color: Color, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .clip(CircleShape)
            .background(color)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "",
            tint = Color.White
        )
    }
}


@AppPreviews
@Composable
private fun HomeScreenPreview() {
    val liloColor =
    LiloPreviewWrapper {
        AppCircleButton(color = LiloFeature.TASKS.accentColor(LiloExtendedTheme.colors), onClick = {})
    }
}