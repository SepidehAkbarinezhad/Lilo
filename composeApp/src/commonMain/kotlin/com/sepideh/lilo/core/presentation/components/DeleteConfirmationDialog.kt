package com.sepideh.lilo.core.presentation.components

import com.sepideh.lilo.ui.theme.*

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sepideh.lilo.core.presentation.TextType
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

/** Feature-neutral confirmation; callers own deletion and its consequences. */
@Composable
fun DeleteConfirmationDialog(
    accent: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = stringResource(Res.string.delete_confirmation_title),
    message: String = stringResource(Res.string.delete_item_confirmation),
    confirmFirst: Boolean = false,
) {
    Dialog(onDismissRequest = onDismiss) {
        FeatureConfirmationContent(accent, title, message, onConfirm, onDismiss, confirmFirst = confirmFirst)
    }
}

@Composable
fun FeatureConfirmationContent(
    accent: Color,
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    confirmLabel: String = stringResource(Res.string.delete_action),
    cancelLabel: String = stringResource(Res.string.cancel_button),
    onCancel: () -> Unit = onDismiss,
    confirmFirst: Boolean = false,
) {
    Surface(modifier = modifier.widthIn(max = 360.dp), shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppText(text = title, textType = TextType.SectionTitle)
            if (message.isNotBlank()) AppText(text = message, textType = TextType.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp, androidx.compose.ui.Alignment.End)) {
                val actionOrder = if (confirmFirst) listOf(true, false) else listOf(false, true)
                actionOrder.forEach { isConfirm ->
                    if (isConfirm) {
                        Button(onClick = onConfirm, modifier = Modifier.weight(1f), shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = LiloExtendedTheme.colors.onAccent)) {
                            AppText(text = confirmLabel, textType = TextType.Action)
                        }
                    } else {
                        TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                            AppText(text = cancelLabel, textType = TextType.Action,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Delete · English", locale = "en", showBackground = true)
@Preview(name = "Delete · Persian", locale = "fa", showBackground = true)
@Composable
private fun DeleteConfirmationPreview() {
    LiloPreviewWrapper {
        FeatureConfirmationContent(Color(0xFFFFC107), stringResource(Res.string.delete_confirmation_title),
            stringResource(Res.string.delete_item_confirmation), {}, {})
    }
}
