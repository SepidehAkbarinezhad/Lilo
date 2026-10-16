package com.sepideh.lilo.settings.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSpacing

@Composable
fun SettingsSection(
    title: String,
    icon: ImageVector,
    headerModifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            Modifier.padding(LiloSpacing.Screen),
            verticalArrangement = Arrangement.spacedBy(LiloSpacing.Item),
        ) {
            SettingsSectionHeader(title, icon, headerModifier, trailingContent)
            content()
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    title: String,
    icon: ImageVector,
    modifier: Modifier,
    trailingContent: @Composable () -> Unit,
) {
    val colors = LiloExtendedTheme.colors
    Row(
        modifier.fillMaxWidth().heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            Modifier.size(40.dp).background(colors.brand.copy(alpha = .07f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(23.dp), tint = colors.brand)
        }
        AppText(text = title, modifier = Modifier.weight(1f).semantics { heading() }, textType = TextType.SectionTitle)
        trailingContent()
    }
}
