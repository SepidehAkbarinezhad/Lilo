package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sepideh.lilo.core.presentation.TextType
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Shared permission explanation; the caller owns the permission and save policy. */
@Composable
fun FeaturePermissionDialog(title: String, message: String, accent: Color,
    onSettings: () -> Unit, onWithoutReminder: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(Modifier.widthIn(max = 360.dp), shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AppText(text = title, textType = TextType.SectionTitle)
                AppText(text = message, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Button(onClick = onSettings, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black)) {
                    AppText(text = Res.string.permission_continue, textType = TextType.Action)
                }
                TextButton(onClick = onWithoutReminder, modifier = Modifier.fillMaxWidth()) {
                    AppText(text = Res.string.save_without_reminder, textType = TextType.Action,
                        color = MaterialTheme.colorScheme.onSurface)
                }
                TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    AppText(text = Res.string.cancel_button, textType = TextType.Action,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
