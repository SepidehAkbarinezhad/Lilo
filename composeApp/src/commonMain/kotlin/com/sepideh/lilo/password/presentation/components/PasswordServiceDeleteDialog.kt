package com.sepideh.lilo.password.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sepideh.lilo.core.presentation.components.*
import com.sepideh.lilo.core.presentation.components.group.GroupSelectionRow
import com.sepideh.lilo.password.presentation.list.components.PasswordServiceIcon
import com.sepideh.lilo.password.domain.PasswordService
import com.sepideh.lilo.ui.theme.*
import lilo.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun PasswordServiceDeleteDialog(
    state: PasswordServicesState,
    onReplacement: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val deleting = state.deleting ?: return
    val colors = LiloExtendedTheme.colors
    DeleteConfirmationDialog(
        accent = colors.passwordColor,
        title = stringResource(Res.string.password_service_delete),
        message = stringResource(Res.string.delete_item_confirmation),
        confirmFirst = true,
        confirmEnabled = !state.busy && (state.usage == 0 || state.replacement != null),
        cancelEnabled = !state.busy,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    ) {
        if (state.usage > 0) AppText(
            text = Res.string.password_service_replace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (state.usage > 0) LazyColumn(
            Modifier.fillMaxWidth().heightIn(max = 300.dp),
            verticalArrangement = Arrangement.spacedBy(LiloSpacing.Small),
        ) {
            items(state.services.filter { it.id != deleting.id }, key = { it.id }) { service ->
                GroupSelectionRow(service.displayName(), state.replacement == service.id, colors.passwordColor,
                    enabled = !state.busy, compact = true,
                    leadingIcon = { PasswordServiceIcon(service.id, colors.textSecondary, Modifier.size(LiloSize.SmallIcon)) }) { onReplacement(service.id) }
            }
        }
        if (state.error) AppText(text = Res.string.password_service_error, color = MaterialTheme.colorScheme.error)
    }
}

@AppPreviews
@Composable
private fun PasswordServiceDeletePreview() {
    LiloPreviewWrapper {
        PasswordServiceDeleteDialog(
            PasswordServicesState(deleting = PasswordService("custom", "Personal")), {}, {}, {},
        )
    }
}

@AppPreviews
@Composable
private fun PasswordServiceReplacementPreview() {
    LiloPreviewWrapper {
        PasswordServiceDeleteDialog(
            PasswordServicesState(
                deleting = PasswordService("old", "Personal"), usage = 2,
                services = listOf(PasswordService("old", "Personal"), PasswordService("new", "Work")),
                replacement = "new",
            ), {}, {}, {},
        )
    }
}
