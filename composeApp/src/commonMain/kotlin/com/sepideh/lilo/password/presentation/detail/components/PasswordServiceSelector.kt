package com.sepideh.lilo.password.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import com.sepideh.lilo.core.presentation.TextType
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.form.FormSelectionRow
import com.sepideh.lilo.ui.theme.LiloExtendedTheme
import com.sepideh.lilo.ui.theme.LiloSpacing
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import com.sepideh.lilo.password.presentation.list.components.passwordServiceImageVector

@Composable
internal fun PasswordServiceSelector(selectedName: String?, selectedId: String? = null, enabled: Boolean = true,
    isError: Boolean = false, onClick: () -> Unit) {
    val colors = LiloExtendedTheme.colors
    val label = stringResource(Res.string.password_service) + " *"
    val errorMessage = stringResource(Res.string.password_service_required)
    Column(verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small)) {
        if (selectedName != null) AppText(text = label, textType = TextType.FieldLabel,
            color = if (isError) MaterialTheme.colorScheme.error else if (enabled) colors.textSecondary else colors.textDisabled)
        FormSelectionRow(
            icon = passwordServiceImageVector(selectedId),
            title = selectedName ?: label, value = "", onClick = onClick,
            enabled = enabled, accent = colors.passwordColor,
            modifier = Modifier.semantics {
                if (selectedName != null) contentDescription = label
                if (isError) error(errorMessage)
            },
        )
        if (isError) AppText(text = errorMessage, textType = TextType.FieldError,
            color = MaterialTheme.colorScheme.error)
    }
}

@AppPreviews
@Composable
private fun PasswordServiceSelectorPreview() {
    LiloPreviewWrapper {
        Column(verticalArrangement = Arrangement.spacedBy(LiloSpacing.Section)) {
            PasswordServiceSelector(null, onClick = {})
            PasswordServiceSelector("GitHub", selectedId = "github", onClick = {})
            PasswordServiceSelector(null, isError = true, onClick = {})
        }
    }
}
