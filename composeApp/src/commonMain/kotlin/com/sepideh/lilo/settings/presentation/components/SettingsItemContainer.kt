package com.sepideh.lilo.settings.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsItemContainer(
    icon: DrawableResource,
    title: StringResource,
    content: @Composable RowScope.() -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .padding(16.dp)
    ) {

        Column(modifier = Modifier) {
            SettingsItemHeader(icon = icon, title = title)
            SettingsItemsRow { content() }
        }
    }
}

@Composable
fun SettingsItemHeader(icon: DrawableResource, title: StringResource) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.weight(.2f).aspectRatio(1f),
            painter = painterResource(icon),
            contentDescription = null
        )
        AppText(
            modifier = Modifier.weight(1f),
            text = title,
            textType = TextType.SubTitle
        )
    }
}

@Composable
private fun SettingsItemsRow(content: @Composable RowScope.() -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max), verticalAlignment = Alignment.Top) {
        Spacer(modifier = Modifier.weight(.2f).fillMaxHeight())
        content()
    }
}

@Composable
fun SettingItem(label: StringResource, selected: String, onSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onSelected() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppText(
            text = label,
            modifier = Modifier.padding(start = 6.dp)
        )
        RadioButton(
            selected = stringResource(label) == selected,
            onClick = { onSelected() }
        )
    }
}

