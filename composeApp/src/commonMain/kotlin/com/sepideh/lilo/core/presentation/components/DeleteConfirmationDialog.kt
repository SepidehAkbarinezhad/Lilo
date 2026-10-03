package com.sepideh.lilo.core.presentation.components

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
) {
    Dialog(onDismissRequest = onDismiss) {
        FeatureConfirmationContent(accent, title, message, onConfirm, onDismiss)
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
) {
    Surface(modifier = modifier.widthIn(max = 360.dp), shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            AppText(text = title, textType = TextType.SectionTitle)
            if (message.isNotBlank()) AppText(text = message, textType = TextType.Body,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp, androidx.compose.ui.Alignment.End)) {
                TextButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                    AppText(text = cancelLabel, textType = TextType.Action,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(onClick = onConfirm, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent, contentColor = Color.Black)) {
                    AppText(text = confirmLabel, textType = TextType.Action)
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
