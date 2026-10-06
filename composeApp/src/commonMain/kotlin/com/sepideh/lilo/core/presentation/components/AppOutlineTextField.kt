package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.validation.ValidationStatus
import com.sepideh.lilo.core.presentation.validation.resolveMessage
import com.sepideh.lilo.ui.theme.*

@Composable
fun AppOutlineTextField(containerModifier: Modifier = Modifier, textFieldModifier: Modifier = Modifier,
    textFieldRequired: TextFieldRequired, leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null, visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default, singleLine: Boolean = true, requestFocus: Boolean = false,
    accentColor: Color = MaterialTheme.colorScheme.primary, textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    topAlignedIcon: Boolean = false, maxLines: Int = 1) {
    val focusRequester = remember { FocusRequester() }
    var focused by remember { mutableStateOf(false) }
    val colors = LiloExtendedTheme.colors
    val field = textFieldRequired
    val errorMessage = if (!field.validationStatus.isSuccessful) field.validationStatus.resolveMessage() else null
    LaunchedEffect(requestFocus) { if (requestFocus) focusRequester.requestFocus() }
    Column(containerModifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
        if (field.label.isNotEmpty()) AppText(text = field.label, textType = TextType.FieldLabel,
            color = if (errorMessage != null) MaterialTheme.colorScheme.error else if (!field.enabled) colors.textDisabled else colors.textSecondary)
        val fieldModifier = textFieldModifier.fillMaxWidth().heightIn(min = 60.dp)
            .semantics { contentDescription = field.label; if (errorMessage != null) error(errorMessage) }
        if (topAlignedIcon) {
            Surface(modifier = fieldModifier, shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, if (errorMessage != null) MaterialTheme.colorScheme.error else if (focused) accentColor else MaterialTheme.colorScheme.outlineVariant)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(LiloSpacing.Item)) {
                    if (leadingIcon != null) CompositionLocalProvider(LocalContentColor provides if (field.enabled) colors.textSecondary else colors.textDisabled) { leadingIcon() }
                    BasicTextField(value = field.value, onValueChange = field.onValueChange, enabled = field.enabled, readOnly = field.readOnly,
                        modifier = Modifier.weight(1f).focusRequester(focusRequester).onFocusChanged { focused = it.isFocused },
                        textStyle = textStyle.copy(color = if (field.enabled) colors.textPrimary else colors.textDisabled),
                        keyboardOptions = keyboardOptions, visualTransformation = visualTransformation,
                        cursorBrush = SolidColor(accentColor), singleLine = singleLine, maxLines = maxLines,
                        decorationBox = { inner -> Box { if (field.value.isEmpty() && field.hint.isNotEmpty()) AppText(text = field.hint, color = colors.textPlaceholder); inner() } })
                    if (trailingIcon != null) trailingIcon()
                }
            }
        } else {
            OutlinedTextField(value = field.value, onValueChange = field.onValueChange, enabled = field.enabled, readOnly = field.readOnly,
                modifier = fieldModifier.focusRequester(focusRequester).onFocusChanged { focused = it.isFocused },
                textStyle = textStyle, shape = MaterialTheme.shapes.large, singleLine = singleLine, maxLines = maxLines,
                isError = errorMessage != null, leadingIcon = leadingIcon, trailingIcon = trailingIcon,
                visualTransformation = visualTransformation, keyboardOptions = keyboardOptions,
                placeholder = { if (field.hint.isNotEmpty()) AppText(text = field.hint, color = colors.textPlaceholder) },
                colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface, disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedTextColor = colors.textPrimary, unfocusedTextColor = colors.textPrimary, disabledTextColor = colors.textDisabled,
                    focusedLeadingIconColor = colors.textSecondary, unfocusedLeadingIconColor = colors.textSecondary, disabledLeadingIconColor = colors.textDisabled,
                    focusedBorderColor = accentColor, unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant, cursorColor = accentColor))
        }
        if (errorMessage != null) AppText(text = errorMessage, textType = TextType.FieldError, color = MaterialTheme.colorScheme.error)
    }
}

data class TextFieldRequired(val label: String = "", val value: String, val onValueChange: (String) -> Unit,
    val onDeletePressed: () -> Unit = {}, val onEnterPressed: () -> Unit = {}, val hint: String = "",
    val enabled: Boolean = true, val readOnly: Boolean = false, val validationStatus: ValidationStatus = ValidationStatus())
