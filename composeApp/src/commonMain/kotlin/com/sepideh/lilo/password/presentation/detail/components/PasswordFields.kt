package com.sepideh.lilo.password.presentation.detail.components

import com.sepideh.lilo.password.domain.model.PASSWORD_SECRET_MAX_LENGTH
import com.sepideh.lilo.password.domain.model.PASSWORD_TITLE_MAX_LENGTH
import com.sepideh.lilo.password.domain.model.Password
import com.sepideh.lilo.password.domain.validation.PasswordValidationError

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.icons.LiloIcons
import com.sepideh.lilo.core.presentation.validation.ValidationStatus
import com.sepideh.lilo.password.presentation.detail.PasswordDetailState
import com.sepideh.lilo.password.presentation.detail.PasswordDetailAction
import com.sepideh.lilo.core.presentation.BaseAction
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun PasswordFields(state: PasswordDetailState, onAction: (BaseAction) -> Unit) {
    val accent = LiloExtendedTheme.colors.passwordColor
    val enabled = !state.locked && !state.loading && !state.saving && !state.loadFailed
    val ltr = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Ltr)
    AppOutlineTextField(
        accentColor = accent,
        leadingIcon = { Icon(LiloIcons.Edit, null, Modifier.size(LiloSize.Icon)) },
        textFieldRequired = TextFieldRequired(
            label = stringResource(Res.string.title_label),
            value = state.record.title,
            enabled = enabled,
            onValueChange = { value -> onAction(PasswordDetailAction.OnTitleChanged(value)) },
            validationStatus = validation(
                PasswordValidationError.TitleTooLong in state.validationErrors,
                Res.string.field_max_length, PASSWORD_TITLE_MAX_LENGTH
            )
        )
    )
    AppOutlineTextField(
        accentColor = accent,
        textStyle = ltr,
        keyboardOptions = KeyboardOptions(autoCorrectEnabled = false),
        textFieldRequired = TextFieldRequired(
            label = stringResource(Res.string.password_username),
            value = state.record.username,
            enabled = enabled,
            onValueChange = { value -> onAction(PasswordDetailAction.OnUsernameChanged(value)) })
    )
    if (PasswordValidationError.IdentifierRequired in state.validationErrors) AppText(
        text = Res.string.password_identifier_required,
        color = MaterialTheme.colorScheme.error
    )
    AppText(
        text = Res.string.password_identifier_hint,
        color = LiloExtendedTheme.colors.textSupporting
    )
    AppOutlineTextField(
        accentColor = accent,
        textStyle = ltr,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrectEnabled = false
        ),
        visualTransformation = if (state.visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(
                onClick = { onAction(PasswordDetailAction.OnVisibility) },
                enabled = enabled
            ) {
                Icon(
                    vectorResource(if (state.visible) Res.drawable.lilo_eye_off else Res.drawable.lilo_eye),
                    stringResource(if (state.visible) Res.string.password_hide else Res.string.password_reveal),
                    Modifier.size(LiloSize.Icon),
                    tint = LiloExtendedTheme.colors.textSecondary
                )
            }
        },
        textFieldRequired = TextFieldRequired(
            label = stringResource(Res.string.password_secret),
            value = state.record.secret,
            isRequired = true,
            enabled = enabled,
            onValueChange = { value -> onAction(PasswordDetailAction.OnSecretChanged(value)) },
            validationStatus = if (PasswordValidationError.SecretTooLong in state.validationErrors) validation(
                true, Res.string.field_max_length, PASSWORD_SECRET_MAX_LENGTH
            ) else validation(
                PasswordValidationError.SecretRequired in state.validationErrors,
                args = arrayOf(stringResource(Res.string.password_secret))
            )
        )
    )
    AppOutlineTextField(
        accentColor = accent,
        textStyle = ltr,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Uri,
            autoCorrectEnabled = false
        ),
        textFieldRequired = TextFieldRequired(
            label = stringResource(Res.string.password_website),
            value = state.record.website,
            hint = "example.com",
            enabled = enabled,
            onValueChange = { value -> onAction(PasswordDetailAction.OnWebsiteChanged(value)) },
            validationStatus = validation(
                PasswordValidationError.InvalidWebsite in state.validationErrors,
                Res.string.password_website_invalid
            )
        )
    )
    AppOutlineTextField(
        accentColor = accent,
        singleLine = false,
        maxLines = Int.MAX_VALUE,
        topAlignedIcon = true,
        textFieldModifier = Modifier.heightIn(min = 100.dp),
        leadingIcon = { Icon(LiloIcons.Description, null, Modifier.size(LiloSize.Icon)) },
        textFieldRequired = TextFieldRequired(
            label = stringResource(Res.string.password_notes),
            value = state.record.notes,
            enabled = enabled,
            onValueChange = { value -> onAction(PasswordDetailAction.OnNotesChanged(value)) })
    )
}

@Composable
private fun validation(
    invalid: Boolean,
    messageId: StringResource = Res.string.error_empty_field_label,
    vararg args: Any,
): ValidationStatus = if (invalid) ValidationStatus(
    isSuccessful = false,
    value = stringResource(messageId, *args),
) else ValidationStatus()

@AppPreviews
@Composable
private fun PasswordFieldsPreview() {
    LiloPreviewWrapper {
        androidx.compose.foundation.layout.Column(Modifier.padding(20.dp)) {
            PasswordFields(
                PasswordDetailState(
                    locked = false,
                    record = Password(
                        title = "Mail",
                        username = "hello@example.com",
                        secret = "example"
                    )
                ),
                {})
        }
    }
}

@AppPreviews
@Composable
private fun PasswordFieldsValidationPreview() {
    LiloPreviewWrapper {
        Column(Modifier.padding(20.dp)) {
            PasswordFields(
                PasswordDetailState(
                    locked = false,
                    record = Password(secret = " ".repeat(PASSWORD_SECRET_MAX_LENGTH + 1), website = "invalid"),
                    validationErrors = setOf(
                        PasswordValidationError.IdentifierRequired,
                        PasswordValidationError.SecretRequired,
                        PasswordValidationError.SecretTooLong,
                        PasswordValidationError.InvalidWebsite,
                    ),
                ),
                {},
            )
        }
    }
}
