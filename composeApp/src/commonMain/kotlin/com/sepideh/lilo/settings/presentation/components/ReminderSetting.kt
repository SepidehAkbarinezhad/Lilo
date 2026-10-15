package com.sepideh.lilo.settings.presentation.components

import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun ReminderSetting(checked: Boolean, enabled: Boolean, onChange: (Boolean) -> Unit) {
    val colors = LiloExtendedTheme.colors
    SettingsSection(
        title = stringResource(Res.string.settings_reminders_title),
        icon = LiloIcons.Bell,
        headerModifier = Modifier.toggleable(checked, enabled = enabled, role = Role.Switch, onValueChange = onChange),
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = colors.brand,
                    checkedThumbColor = MaterialTheme.colorScheme.surface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    uncheckedBorderColor = colors.textSupporting,
                    uncheckedThumbColor = colors.textSupporting,
                ),
            )
        },
    ) {
        AppText(text = Res.string.settings_reminders_description, textType = TextType.Caption,
            color = colors.textSecondary)
    }
}
