package com.sepideh.lilo.core.presentation.components.group

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSize
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Shared add/rename controls for groups and services. */
@Composable
fun InlineEditorActions(name: String, busy: Boolean, onConfirm: () -> Unit, onCancel: () -> Unit) {
    val colors = LiloExtendedTheme.colors
    val canConfirm = name.isNotBlank() && !busy
    IconButton(onClick = onConfirm, enabled = canConfirm) {
        if (busy) CircularProgressIndicator(Modifier.size(LiloSize.SmallIcon), color = colors.success)
        else Icon(LiloIcons.Check, stringResource(Res.string.confirm_action),
            tint = if (canConfirm) colors.success else colors.textDisabled)
    }
    IconButton(onClick = onCancel, enabled = !busy) {
        Icon(LiloIcons.Close, stringResource(Res.string.cancel_button),
            tint = if (busy) colors.textDisabled else MaterialTheme.colorScheme.error)
    }
}
