package com.sepideh.lilo.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.ValidationStatus
import com.sepideh.lilo.core.presentation.resolveMessage

@Composable
fun AppOutlineTextField(
    modifier: Modifier = Modifier,
    textFieldRequired: TextFieldRequired,
    leadingIconBackgroundColor: Color = Color.Transparent,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    requestFocus: Boolean = false,
    isLTR: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        if (requestFocus)
            focusRequester.requestFocus()
    }
    var isFocused by remember {
        mutableStateOf(false)
    }

    val focusedColor = if (isFocused) MaterialTheme.colorScheme.primary else Gray

    with(textFieldRequired) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AppText(
                modifier = modifier,
                text = label,
                textType = TextType.SubTitle,
                color = focusedColor,
            )
            Box(
                modifier = modifier.border(
                    width = 1.dp,
                    color = focusedColor,
                    shape = MaterialTheme.shapes.small
                )
            ) {
                TextField(
                    modifier = modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { isFocused = it.isFocused },
                    value = value,
                    onValueChange = onValueChange,
                    enabled = enabled,
                    readOnly = textFieldRequired.readOnly,
                    textStyle = textStyle.copy(textDirection = if (isLTR) TextDirection.Ltr else TextDirection.Rtl),
                    placeholder = {
                        if (hint.isNotEmpty()) AppText(
                            modifier = Modifier,
                            text = hint,
                            textType = TextType.Body
                        )
                    },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    isError = !validationStatus.isSuccessful,
                    visualTransformation = visualTransformation,
                    keyboardOptions = keyboardOptions,
                    singleLine = singleLine,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = White,
                        focusedContainerColor = White,
                        unfocusedIndicatorColor = Transparent,
                        focusedIndicatorColor = Transparent
                    ),
                    shape = RoundedCornerShape(size = 20.dp)
                )
            }


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