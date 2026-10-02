package com.sepideh.lilo.core.presentation.components.picker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.AppText
import com.sepideh.lilo.core.presentation.components.LiloPreviewWrapper
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FeatureTimePicker(hour: Int, minute: Int, accent: Color, onConfirm: (Int, Int) -> Unit, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        FeatureTimePickerContent(hour, minute, accent, onConfirm, onDismiss)
    }
}

@Composable
fun FeatureTimePickerContent(hour: Int, minute: Int, accent: Color, onConfirm: (Int, Int) -> Unit, onDismiss: () -> Unit) {
    var hours by rememberSaveable(hour) { mutableStateOf(hour.toString().padStart(2, '0')) }
    var minutes by rememberSaveable(minute) { mutableStateOf(minute.toString().padStart(2, '0')) }
    val selectedHour = hours.toIntOrNull()
    val selectedMinute = minutes.toIntOrNull()
    val valid = selectedHour != null && selectedHour in 0..23 && selectedMinute != null && selectedMinute in 0..59
    Surface(shape = RoundedCornerShape(20.dp), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            AppText(text = Res.string.reminder_time_title, textType = TextType.SectionTitle, color = accent)
            // Keep conventional HH MM order while the surrounding dialog follows app direction.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)) {
                    TimeNumberField(hours, { hours = it }, stringResource(Res.string.hour_label), 23, accent)
                    TimeNumberField(minutes, { minutes = it }, stringResource(Res.string.minute_label), 59, accent)
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) {
                    AppText(text = Res.string.cancel_button, textType = TextType.Action, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                TextButton(onClick = { if (valid) onConfirm(selectedHour!!, selectedMinute!!) }, enabled = valid,
                    colors = ButtonDefaults.textButtonColors(contentColor = accent)) {
                    AppText(text = Res.string.confirm_action, textType = TextType.Action)
                }
            }
        }
    }
}

@Composable
private fun TimeNumberField(value: String, onChange: (String) -> Unit, label: String, maximum: Int, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppText(text = label, textType = TextType.FieldLabel)
        OutlinedTextField(value = value, onValueChange = { input ->
            // Accept both Persian and Latin digits; store a normalized numeric value.
            val digits = input.mapNotNull { it.digitToIntOrNull() }
            if (input.length <= 2 && digits.size == input.length) {
                val normalized = digits.joinToString("")
                if (normalized.isEmpty() || normalized.toInt() <= maximum) onChange(normalized)
            }
        }, modifier = Modifier.width(80.dp), singleLine = true,
            shape = RoundedCornerShape(12.dp), textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = accent, unfocusedBorderColor = accent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface, cursorColor = accent))
    }
}

@Preview(name = "Time · Persian", locale = "fa", showBackground = true, widthDp = 360)
@Preview(name = "Time · English", locale = "en", showBackground = true, widthDp = 360)
@Composable
private fun FeatureTimePickerPreview() {
    LiloPreviewWrapper { FeatureTimePickerContent(9, 26, Color(0xFFFFC107), { _, _ -> }, {}) }
}
