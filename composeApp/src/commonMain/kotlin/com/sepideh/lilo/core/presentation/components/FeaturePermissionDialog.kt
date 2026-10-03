package com.sepideh.lilo.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Explicit Cancel and outside/back dismissal have different caller-owned outcomes. */
@Composable
fun FeaturePermissionDialog(title: String, message: String, accent: Color,
    onSettings: () -> Unit, onCancel: () -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        FeatureConfirmationContent(accent, title, message, onSettings, onDismiss,
            confirmLabel = stringResource(Res.string.permission_continue),
            cancelLabel = stringResource(Res.string.cancel_button), onCancel = onCancel)
    }
}
