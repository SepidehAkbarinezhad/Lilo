package com.sepideh.lilo.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditCancellationDialog(accent: Color, onKeepEditing: () -> Unit, onDiscard: () -> Unit) {
    Dialog(onDismissRequest = onKeepEditing) {
        FeatureConfirmationContent(accent, stringResource(Res.string.edit_cancel_title),
            stringResource(Res.string.note_discard_message), onKeepEditing, onKeepEditing,
            confirmLabel = stringResource(Res.string.note_keep_editing),
            cancelLabel = stringResource(Res.string.edit_cancel_action), onCancel = onDiscard)
    }
}
