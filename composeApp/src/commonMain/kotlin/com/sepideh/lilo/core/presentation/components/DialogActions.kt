package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.ui.theme.LiloExtendedTheme

/** Compact actions in reading order: main action at start, cancellation at end. */
@Composable
fun DialogActions(
    accent: Color,
    confirmLabel: String,
    cancelLabel: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    confirmEnabled: Boolean = true,
    cancelEnabled: Boolean = true,
) {
    Row(modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = onConfirm, enabled = confirmEnabled, shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(containerColor = accent,
                contentColor = LiloExtendedTheme.colors.onAccent)) {
            AppText(text = confirmLabel, textType = TextType.Action)
        }
        TextButton(onClick = onCancel, enabled = cancelEnabled,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)) {
            AppText(text = cancelLabel, textType = TextType.Action)
        }
    }
}
