package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import org.jetbrains.compose.resources.StringResource

@Composable
fun ListEmptyState(message: StringResource, accent: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(shape = CircleShape, color = accent.copy(alpha = .08f)) {
                Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                    Icon(icon, null, Modifier.size(36.dp), tint = LiloExtendedTheme.colors.textSupporting)
                }
            }
            AppText(text = message, color = LiloExtendedTheme.colors.textSecondary, textAlign = TextAlign.Center)
        }
    }
}
