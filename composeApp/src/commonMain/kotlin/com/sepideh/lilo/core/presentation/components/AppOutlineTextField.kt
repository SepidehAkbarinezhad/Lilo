package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDirection
import com.sepideh.lilo.core.presentation.validation.ValidationStatus
import com.sepideh.lilo.core.presentation.validation.resolveMessage
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.ui.theme.Amber600

@Composable
fun AppOutlineTextField(
    containerModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    textFieldRequired: TextFieldRequired,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    requestFocus: Boolean = false,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = LocalTextStyle.current,
    maxLines: Int = 1
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (requestFocus) {
            focusRequester.requestFocus()
        }
    }

    var isFocused by remember {
        mutableStateOf(false)
    }

    with(textFieldRequired) {

        val labelColor = when {
            !validationStatus.isSuccessful ->
                MaterialTheme.colorScheme.error

            isFocused ->
                accentColor

            else ->
                MaterialTheme.colorScheme.onSurfaceVariant
        }

        Column(
            modifier = containerModifier.fillMaxWidth()
        ) {
            AppText(
                text = label,
                textType = TextType.SubTitle,
                color = labelColor
            )

            OutlinedTextField(
                modifier = textFieldModifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        isFocused = it.isFocused
                    },
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                placeholder = {
                    if (hint.isNotEmpty()) {
                        AppText(
                            text = hint,
                            textType = TextType.Body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },

                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                isError = !validationStatus.isSuccessful,
                maxLines = maxLines,

                shape = MaterialTheme.shapes.medium,

                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,

                    focusedBorderColor = accentColor,
                    focusedLeadingIconColor = accentColor,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedBorderColor =
                        MaterialTheme.colorScheme.outlineVariant,

                    cursorColor = accentColor,

                    focusedTextColor =
                        MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor =
                        MaterialTheme.colorScheme.onSurface,

                    focusedPlaceholderColor =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedPlaceholderColor =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            if (!validationStatus.isSuccessful) {
                AppText(
                    text = validationStatus.resolveMessage(),
                    textType = TextType.FieldError,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

data class TextFieldRequired(
    val label: String = "",
    val value: String,
    val onValueChange: (String) -> Unit,
    val onDeletePressed: () -> Unit = {},
    val onEnterPressed: () -> Unit = {},
    val hint: String = "",
    val enabled: Boolean = true,
    val readOnly: Boolean = false,
    val validationStatus: ValidationStatus = ValidationStatus()
)