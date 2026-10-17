package com.sepideh.lilo.home.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.format.localizedDigits
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import org.jetbrains.compose.resources.StringResource

@Composable
internal fun HomeCount(value: Int, label: StringResource, modifier: Modifier = Modifier) {
    val colors = LiloExtendedTheme.colors
    val persian = LocalLayoutDirection.current == LayoutDirection.Rtl
    Column(modifier.padding(horizontal = 4.dp), horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(value.toString().localizedDigits(persian), style = MaterialTheme.typography.headlineMedium, color = colors.title)
        AppText(text = label, textType = TextType.Caption, color = colors.textSecondary, textAlign = TextAlign.Center)
    }
}

@Composable
internal fun HomeSummaryDivider() {
    VerticalDivider(Modifier.height(48.dp), color = LiloExtendedTheme.colors.textSupporting.copy(alpha = .28f))
}

@Composable
internal fun HomeLatestSummary(count: Int, countLabel: StringResource, latestTitle: String,
    timestamp: Long?, emptyLabel: StringResource, onClick: () -> Unit, onLatestClick: (() -> Unit)?,
    leadingIcon: (@Composable () -> Unit)? = null, subtitle: String? = null) {
    val colors = LiloExtendedTheme.colors
    Surface(shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = .65f)) {
        Row(Modifier.fillMaxWidth().heightIn(min = 84.dp).padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            HomeCount(count, countLabel, Modifier.weight(.38f).clickable(role = Role.Button, onClick = onClick))
            HomeSummaryDivider()
            Column(Modifier.weight(.62f).clickable(enabled = count > 0 && onLatestClick != null, role = Role.Button) { onLatestClick?.invoke() }.padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.Start) {
                if (count == 0) AppText(text = emptyLabel, textType = TextType.Caption, color = colors.textSecondary)
                else {
                    if (leadingIcon == null) {
                        AppText(text = latestTitle, textType = TextType.BodyLarge, color = colors.title, maxLines = 2)
                    } else Row(Modifier.fillMaxWidth().heightIn(min = 28.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)) {
                        leadingIcon()
                        AppText(text = latestTitle, modifier = Modifier.weight(1f, fill = false),
                            textType = TextType.BodyLarge, color = colors.title, maxLines = 2)
                    }
                    (subtitle ?: homeDateLabel(timestamp))?.let {
                        AppText(text = it, textType = TextType.Caption, color = colors.textSecondary, maxLines = if (subtitle != null) 1 else Int.MAX_VALUE)
                    }
                }
            }
        }
    }
}
